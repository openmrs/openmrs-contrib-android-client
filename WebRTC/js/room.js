function Room(roomId, session, token, chattr){
  this.roomId = roomId;
  this.session = session;
  this.token = token;
  this.chattr = chattr;
  this.filterData = {};
  this.subscribers = {};
  this.myStream = undefined;
  this.publisher = undefined;
  this.focussedConnectionId = undefined;
  this.unseenCount = 0;
  this.initialized = false;
  this.recording = false;
  this.initOT();
  this.init();
}
Room.prototype = {
  constructor: Room,
  init: function(){
    var self = this;
    window.onresize = self.layout;
    $("#chatButton").click(function(){
      $(".container").toggleClass("chatEnabled");
      $("#chattr").toggleClass("chatEnabled");
      $('#chatInput').focus();
      $('.container').on('transitioned webkitTransitionEnd', function(e){
        self.layout();
      });
      self.unseenCount = 0;
      $("#chatButton").addClass("no-after");
    });
    $('#chatInput').keyup(function(e){
      if(e.keyCode == 27){$('#chatButton').trigger('click');}
    });
    $("#recordButton").click(function(){
      $(this).toggleClass("selected");
      var actionVerb, nextAction;
      if($(this).hasClass("selected")){
        self.triggerActivity("record","start");
        actionVerb = "started";
        nextAction = "Stop";
      } else{ 
        self.triggerActivity("record","stop");
        actionVerb = "stopped";
        nextAction = "Start";
      }
      $("#recordButton").data('tooltip').options.title=nextAction+" Recording";
    });
    $(document.body).on("click","#filtersList li button",function(){
      $("#filtersList li button").removeClass("selected");
      var prop = $(this).data('value');
      self.applyClassFilter( prop, "#myPublisher" );
      $(this).addClass("selected");
      self.sendSignal( "filter", {cid: self.session.connection.connectionId, filter: prop });
      self.filterData[self.session.connection.connectionId] = prop;
    });
  },
  initOT: function(){
    var _this = this;
    var session = this.session;
    session.connect(this.token, function(error){
      _this.publisher = OT.initPublisher( "<%= apiKey %>", "myPublisher", {width:"100%", height:"100%"} );
      _this.publisher.on('streamDestroyed', function() {
        _this.publisher = undefined;
        _this.myStream = undefined;
      });
      session.publish( _this.publisher, function(err) {
        if (err) return console.log('publishing error');
        _this.myStream = _this.publisher.stream;
      });
      setTimeout(function(){_this.initialized = true;}, 2000);
    });
    session.on("sessionDisconnected", function(event){
      var msg = (event.reason === "forceDisconnected") ? "Someone in the room found you offensive and removed you. Please evaluate your behavior" : "You have been disconnected! Please try again";
      alert(msg);
      window.location = "/";
    });
    session.on("streamCreated", function(event){
      var streamConnectionId = event.stream.connection.connectionId;
      // create new div container for stream, subscribe, apply filter
      var divId = "stream" + streamConnectionId;
      $("#streams_container").append(
         _this.userStreamTemplate({ id: divId, connectionId: streamConnectionId }) );
      _this.subscribers[ streamConnectionId ] = session.subscribe( event.stream, divId , {width:"100%", height:"100%"} );
      _this.subscribers[streamConnectionId].on('destroyed', function(event) {
        delete _this.subscribers[streamConnectionId];
      });
      var divId$ = $("."+divId);
      divId$.mouseenter(function(){
        $(this).find('.flagUser').show();
      });
      divId$.mouseleave(function(){
        $(this).find('.flagUser').hide();
      });

      // mark user as offensive
      divId$.find('.flagUser').click(function(){
        var streamConnection = $(this).data('streamconnection');
        if(confirm("Is this user being inappropriate? If so, click confirm to remove user")){
          _this.applyClassFilter("Blur", "."+streamConnection);
          _this.session.forceDisconnect( streamConnection.split("stream")[1] );
        }
      }); 

      // TODO: might not be needed
      _this.applyFocus();
      _this.layout();
    });
    session.on("streamDestroyed", function(event){
      _this.removeStream(event.stream.connection.connectionId);

      // TODO: might not be needed
      _this.applyFocus();
      _this.layout();
    });
    session.on("connectionCreated", function(event){
      if(_this.initialized){
        var dataToSend = {"filterData": _this.filterData};
        if(_this.archiveId && $(".controlOption[data-activity=record]").hasClass("selected")){
          dataToSend.archiveId = _this.archiveId;
        }
        if (_this.focussedConnectionId) {
          dataToSend.focussedConnectionId = _this.focussedConnectionId;
        }
        _this.sendSignal("initialize", dataToSend, event.connection);
      }
    });
    session.on("signal", function(event){
      var data = JSON.parse( event.data );
      switch(event.type){
        case "signal:initialize":
          if(!_this.initialized){
            _this.filterData = data.filterData;
            _this.applyAllFilters();
            if(data.archiveId){
              _this.archiveId = data.archiveId;
              $(".controlOption[data-activity=record]").addClass('selected');
              $("#recordButton").data('tooltip').options.title="Stop Recording";
            }

            if (data.focussedConnectionId) {
              _this.focus(data.focussedConnectionId);
            }

            _this.initialized = true;
          }
          break;
        case "signal:archive":
          var actionVerb, newAction;
          if(data.action === "start"){
            actionVerb = "started";
            newAction = "Stop";
            $(".controlOption[data-activity=record]").addClass('selected');
          } else{
            actionVerb = "stopped";
            newAction = "Start";
            $(".controlOption[data-activity=record]").removeClass('selected');
          }
          $("#recordButton").data('tooltip').options.title=newAction+" Recording";
          _this.archiveId = data.archiveId;
          var archiveUrl = window.location.origin +"/archive/"+_this.archiveId+"/"+_this.roomId;
          var msg = {"type": "generalUpdate", "data":{"text":"Archiving for this session has "+actionVerb+". View it here: <a href = '"+ archiveUrl+"'>"+archiveUrl+"</a>"}};
          _this.chattr.messages.push(msg);
          _this.chattr.printMessage(msg);
          break;
        case "signal:filter":
          _this.filterData[data.cid] = data.filter;
          _this.applyClassFilter(data.filter, ".stream"+data.cid);
          break;
        case "signal:chat":
          if(!($("#chatButton").hasClass('selected'))){
            _this.unseenCount+=1;
            $("#chatButton").attr("data-unseen-count", _this.unseenCount);
            $("#chatButton").removeClass("no-after");
          } 
          break;
      }
    });
  },
  layout : OT.initLayoutContainer( document.getElementById( "streams_container"), {
      fixedRatio: true,
      animate: true,
      bigClass: "OT_big",
      bigPercentage: 0.85,
      bigFixedRatio: false,
      easing: "swing"
    }).layout,
  removeStream : function(cid){
    $(".stream"+cid).remove();
  },
  userStreamTemplate : Handlebars.compile( $("#userStreamTemplate").html() ),
  triggerActivity : function(activity, action){
    switch(activity){
      case "record":
        var data = {action: action, roomId: this.roomId}; // room Id needed for room servation credentials on server
        if(this.archiveId){
          data.archiveId = this.archiveId;
        }
        var self = this;
        $.post("/archive/"+this.session.sessionId, data, function(response){
          if(response.id){
            self.archiveId = response.id;
            if(action == "start")
              self.archiving = true;
            else
              self.archiving = false;
            var signalData = {name: self.name, archiveId: response.id, action: action};
            self.sendSignal("archive",signalData);
          }
        });
        break;
    }
  },
  sendSignal: function( type, data, to ){
    var signalData = {type: type, data: JSON.stringify(data)};
    if(to){
      signalData.to = to;
    }
    this.session.signal( signalData, this.errorSignal );
  },
  errorSignal: function(error){
    if(error){
      console.log("signal error: " + error.reason);
    }
  },
  applyAllFilters: function(){
    for(cid in this.filterData){
      this.applyClassFilter(this.filterData[cid], ".stream"+cid);
    }
  },
  applyClassFilter: function(prop, selector){
    if(prop){
      $(selector).removeClass( "Blur Sepia Grayscale Invert" );
      $(selector).addClass( prop );
    }
  },

  applyFocus: function() {
    var self = this;
    var focussedStreamContainer, focussedWidget;

    if (this.focussedConnectionId) {

      if (this.focussedConnectionId === this.session.connection.connectionId) {
        focussedStreamContainer = $('#myPublisherContainer');
        focussedWidget = this.publisher;
      } else {
        focussedStreamContainer = $('#stream'+this.focussedConnectionId).parent();
        focussedWidget = this.subscribers[this.focussedConnectionId];
      }

      // if the stream to focus on already has class 'OT_big', we assume its already been focussed
      if (focussedStreamContainer.length === 1 && !focussedStreamContainer.hasClass('OT_big')) {

        focussedStreamContainer.addClass('OT_big');
        if (focussedWidget instanceof OT.Subscriber) {
          focussedWidget.restrictFrameRate(false);
        }
      }

      $('.streamContainer').not(focussedStreamContainer).removeClass('OT_big').each(function() {
        // `this` refers to the iterated element
        // call restrictFrameRate() on each subscriber
        var connectionId = findConnectionIdFromElement(this);
        // if there was no connectionId found, assume the element contains the publisher and skip
        if (connectionId) {
          var subscriber = self.subscribers[connectionId];
          if (!subscriber) {
            return console.error('cannot find subscriber for element:', this);
          }
          subscriber.restrictFrameRate(true);
        }
      });

    } else {

      $('.streamContainer').removeClass('OT_big');
      for (var connectionId in this.subscribers) {
        if (this.subscribers.hasOwnProperty(connectionId)) {
          this.subscribers[connectionId].restrictFrameRate(false);
        }
      }

    }
  },

  focus: function(connectionId) {
    this.focussedConnectionId = connectionId;
    this.applyFocus();
    this.layout();
  },
  unfocus: function() {
    this.focussedConnectionId = undefined;
    this.applyFocus();
    this.layout();
  }

};

var findConnectionIdFromElement = function(el) {
  var className;
  for (var i = 0; i < el.classList.length; i++) {
    className = el.classList[i];
    if (className !== 'streamContainer' && className.indexOf('stream') === 0) {
      return className.substr(6);
    }
  }
  return undefined;
};
