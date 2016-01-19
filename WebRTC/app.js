// ***
// *** Required modules
// ***
var express    = require('express'),
    opentok    = require('opentok'),
    bodyParser = require('body-parser'), // middleware
    cors       = require('cors'),
    config     = require("./config"),
    storage    = require('./lib/store.js'),
    loadMiddleware    = require('./lib/load-middleware.js');

// ***
// *** OpenTok Constants for creating Session and Token values
// ***
var OTKEY = config.opentok.key;
var ot = new opentok(config.opentok.key, config.opentok.secret);

// ***
// *** Setup Express to handle static files in public folder
// *** Express is also great for handling url routing
// ***
var app = express();
app.set( 'views', __dirname + "/views");
app.set( 'view engine', 'ejs' );
app.use(bodyParser.urlencoded());
app.use(bodyParser.json());
app.use(express.static(__dirname + '/public'));

// ***
// *** Load middleware
// ***
//app.use(cors({methods:'GET'}));
storage.init(config); // setup memory or redis, depending on config
// prevent iframe embedding
app.use(function(req, res, next) {
  res.header('X-Frame-Options', 'SAMEORIGIN');
  next();
});
loadMiddleware(app, config);

// ***
// *** When user goes to root directory, render index page
// ***
app.get("/", function( req, res ){
  res.render('index');
});

// ***
// *** Post endpoint to start/stop archives
// ***
app.post('/archive/:sessionId', function(req, res, next) {
  // final function to be called when all the necessary data is gathered
  function sendArchiveResponse(error, archive) {
    if (error) {
      var payload;
      if (config.web.env === 'development') {
        payload = { error: error.message };
      } else {
        payload = { error: 'An error occurred, could not ' + req.body.action + ' the archive' };
      }

      return res.json(500, payload);
    }

    res.json(archive);
  }

  // When an archive is given through a reservation
  if( req.archiveInfo ){
    sendArchiveResponse( req.archiveInfo.error, req.archiveInfo.archive );
    return;
  }

  // When an archive needs to be created or stopped
  if( req.body.action === "start" ){
    ot.startArchive(req.params.sessionId, {name: req.body.roomId}, sendArchiveResponse);
  }else{
    ot.stopArchive(req.body.archiveId, sendArchiveResponse);
  }
});

// ***
// *** Renders archive page
// ***
app.get('/archive/:archiveId/:roomId', function(req, res, next) {
  // final function to be called when all the necessary data is gathered
  function sendArchiveResponse(error, archive) {
    if (error) {
      var payload;
      if (config.web.env === 'development') {
        payload = { error: error.message };
      } else {
        payload = { error: 'An error occurred, could not get the archive' };
      }
      // NOTE: see quirk note below. applies for this property as well.
      payload.archive = false;
      console.log(payload);
      return res.json(500, payload);
    }

    // NOTE: sending 'error: false' in the response is unnecessary. this quirk should be removed
    //       but if clients depend on this behavior then that needs to be changed across the
    //       clients before making the change.
    return res.render('archive', {error: false, archive: archive});

  }

  // When an archive is given through a reservation
  if( req.archiveInfo ){
    sendArchiveResponse( req.archiveInfo.error, req.archiveInfo.archive );
    return;
  }

  // When an archive needs to be created or stopped
  ot.getArchive(req.params.archiveId, sendArchiveResponse);
});

// ***
// *** When user goes to a room, render the room page
// ***
app.get("/:rid", function( req, res ){
  // final function to be called when all the necessary data is gathered
  var sendRoomResponse = function(apiKey, sessionId, token) {
    var data = {
      rid: rid,
      sid: sessionId,
      sessionId: sessionId,
      apiKey : apiKey,
      token: token
    };
    if (req.format === 'json') {
      res.json(data);
    } else {
      res.render('room', data);
    }
  };

  console.log(req.url);

  var rid = req.params.rid.split('.json')[0];
  var room_uppercase = rid.toUpperCase();

  // When a room is given through a reservation
  if (req.sessionId && req.apiKey && req.token) {
    sendRoomResponse(req.apiKey, req.sessionId, req.token);
  } else {
    // Check if room sessionId exists. If it does, render response. If not, create sessionId
    storage.get(room_uppercase, function(reply){
      if(reply){
        req.sessionId = reply;
        sendRoomResponse(OTKEY, req.sessionId, ot.generateToken(req.sessionId, {role: 'moderator'}));
      }else{
        ot.createSession( req.sessionProperties || {mediaMode: 'routed'} , function(err, session){
          if (err) {
            var payload;
            if (config.web.env === 'development') {
              payload = { error: err.message };
            } else {
              payload = { error: 'could not generate opentok session' };
            }

            return res.send(500, payload);
          }

          storage.set(room_uppercase, session.sessionId, function(){
            sendRoomResponse(OTKEY, session.sessionId, ot.generateToken(session.sessionId, {role: 'moderator'}));
          });
        });
      }
    });

  }
});


// ***
// *** start server, listen to port (predefined or 9393)
// ***
app.listen(config.web.port, function() {
  console.log('application now served on port ' + config.web.port +
    ' in ' + config.web.env + ' environment');
});
