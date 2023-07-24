package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.api.RestServiceBuilder
import com.openmrs.android_sdk.library.api.repository.LocationRepository
import com.openmrs.android_sdk.library.api.repository.LoginRepository
import com.openmrs.android_sdk.library.api.repository.VisitRepository
import com.openmrs.android_sdk.library.dao.LocationDAO
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.OperationType
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.library.models.Session
import com.openmrs.android_sdk.library.models.User
import com.openmrs.android_sdk.library.models.VisitType
import com.openmrs.android_sdk.utilities.NetworkUtils
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mindrot.jbcrypt.BCrypt
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.openmrs.mobile.activities.login.LoginViewModel
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.services.UserService
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class LoginViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var loginRepository: LoginRepository

    @Mock
    lateinit var visitRepository: VisitRepository

    @Mock
    lateinit var locationRepository: LocationRepository

    @Mock
    lateinit var locationDAO: LocationDAO

    @Mock
    lateinit var userService: UserService

    lateinit var viewModel: LoginViewModel

    private val initialUrl = "http://www.some_server_url.com"
    private lateinit var OpenmrsAndroidMock: MockedStatic<OpenmrsAndroid>
    private lateinit var OpenMRSMock: MockedStatic<OpenMRS>
    private lateinit var NetworkUtilsMock: MockedStatic<NetworkUtils>
    private lateinit var BCryptMock: MockedStatic<BCrypt>

    @Before
    override fun setUp() {
        super.setUp()
        OpenmrsAndroidMock = Mockito.mockStatic(OpenmrsAndroid::class.java)
        `when`(OpenmrsAndroid.getServerUrl()).thenReturn(initialUrl)

        OpenMRSMock = Mockito.mockStatic(OpenMRS::class.java)
        `when`(OpenMRS.getInstance()).thenReturn(OpenMRS())

        NetworkUtilsMock = Mockito.mockStatic(NetworkUtils::class.java)
        BCryptMock = Mockito.mockStatic(BCrypt::class.java)

        viewModel = LoginViewModel(loginRepository, visitRepository, locationRepository, locationDAO, userService)
    }

    @After
    override fun tearDown() {
        super.tearDown()
        OpenmrsAndroidMock.close()
        OpenMRSMock.close()
        NetworkUtilsMock.close()
        BCryptMock.close()
    }

    @Test
    fun `showWarningOrLogin should not do anything if inputs are not valid`() {
        viewModel.showWarningOrLogin(username = "", password = "password", url = "url", wipeDatabase = false)
        assertNull(viewModel.warningDialogLiveData.value)

        viewModel.showWarningOrLogin(username = "username", password = "", url = "url", wipeDatabase = false)
        assertNull(viewModel.warningDialogLiveData.value)

        viewModel.showWarningOrLogin(username = "username", password = "password", url = "", wipeDatabase = false)
        assertNull(viewModel.warningDialogLiveData.value)

        viewModel.showWarningOrLogin(username = "username", password = "password", url = "url", wipeDatabase = false)
        assertNotNull(viewModel.warningDialogLiveData.value)
    }

    @Test
    fun `showWarningOrLogin should show wipe database warning when different server url is used`() {
        val username = "username123"
        val password = "password123"
        val url = "http://www.some_different_url.com"
        `when`(OpenmrsAndroid.getUsername()).thenReturn(username)
        `when`(OpenmrsAndroid.getServerUrl()).thenReturn(url)
        `when`(OpenmrsAndroid.getHashedPassword()).thenReturn(password)
        `when`(BCrypt.checkpw(any(), any())).thenReturn(true)

        viewModel.showWarningOrLogin(username, password, url, wipeDatabase = false)

        assertTrue(viewModel.warningDialogLiveData.value!!)
    }

    @Test
    fun `showWarningOrLogin should show wipe database warning when different username is used`() {
        val username1 = "username1"
        val username2 = "username2"
        val password = "password123"
        val url = initialUrl
        `when`(OpenmrsAndroid.getUsername()).thenReturn(username1)
        `when`(OpenmrsAndroid.getServerUrl()).thenReturn(url)
        `when`(OpenmrsAndroid.getHashedPassword()).thenReturn(password)
        `when`(BCrypt.checkpw(any(), any())).thenReturn(true)

        viewModel.showWarningOrLogin(username2, password, url, wipeDatabase = false)

        assertTrue(viewModel.warningDialogLiveData.value!!)
    }

    @Test
    fun `showWarningOrLogin should show wipe database warning when different password is used`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        `when`(OpenmrsAndroid.getUsername()).thenReturn(username)
        `when`(OpenmrsAndroid.getServerUrl()).thenReturn(url)
        `when`(OpenmrsAndroid.getHashedPassword()).thenReturn(password)
        `when`(BCrypt.checkpw(any(), any())).thenReturn(false)

        viewModel.showWarningOrLogin(username, password, url, wipeDatabase = false)

        assertTrue(viewModel.warningDialogLiveData.value!!)
    }

    @Test
    fun `showWarningOrLogin should show wipe database warning when wipeDatabase flag is used`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        `when`(OpenmrsAndroid.getUsername()).thenReturn(username)
        `when`(OpenmrsAndroid.getServerUrl()).thenReturn(url)
        `when`(OpenmrsAndroid.getHashedPassword()).thenReturn(password)
        `when`(BCrypt.checkpw(any(), any())).thenReturn(true)

        viewModel.showWarningOrLogin(username, password, url, wipeDatabase = true)

        assertTrue(viewModel.warningDialogLiveData.value!!)
    }


    @Test
    fun `showWarningOrLogin should not show wipe database warning when login credentials are the same as the last one and wipeDatabase flag is not used`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        `when`(OpenmrsAndroid.getUsername()).thenReturn(username)
        `when`(OpenmrsAndroid.getServerUrl()).thenReturn(url)
        `when`(OpenmrsAndroid.getHashedPassword()).thenReturn(password)
        `when`(BCrypt.checkpw(any(), any())).thenReturn(true)

        viewModel.showWarningOrLogin(username, password, url, wipeDatabase = false)

        assertFalse(viewModel.warningDialogLiveData.value!!)
    }

    @Test
    fun `login should result LoginOfflineSuccess when not online and user has logged in before and the credentials are valid`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        val lastSessionToken = "session token"
        `when`(NetworkUtils.isOnline()).thenReturn(false)
        `when`(OpenmrsAndroid.isUserLoggedOnline()).thenReturn(true)
        `when`(OpenmrsAndroid.getLastLoginServerUrl()).thenReturn(url)
        `when`(OpenmrsAndroid.getUsername()).thenReturn(username)
        `when`(OpenmrsAndroid.getHashedPassword()).thenReturn(password)
        `when`(OpenmrsAndroid.getLastSessionToken()).thenReturn(lastSessionToken)
        `when`(BCrypt.checkpw(any(), any())).thenReturn(true)

        viewModel.login(username, password, url, false)

        val resultType = (viewModel.result.value as Result.Success).data
        assertEquals(ResultType.LoginOfflineSuccess, resultType)
        OpenmrsAndroidMock.verify { OpenmrsAndroid.deleteSecretKey() }
        OpenmrsAndroidMock.verify { OpenmrsAndroid.setPasswordAndHashedPassword(password) }
        OpenmrsAndroidMock.verify { OpenmrsAndroid.setSessionToken(lastSessionToken) }
    }

    @Test
    fun `login should result LoginInvalidCredentials when not online and user has logged in before and the credentials are invalid`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        val lastSessionToken = "session token"
        `when`(NetworkUtils.isOnline()).thenReturn(false)
        `when`(OpenmrsAndroid.isUserLoggedOnline()).thenReturn(true)
        `when`(OpenmrsAndroid.getLastLoginServerUrl()).thenReturn(url)
        `when`(OpenmrsAndroid.getUsername()).thenReturn(username)
        `when`(OpenmrsAndroid.getLastSessionToken()).thenReturn(lastSessionToken)
        `when`(BCrypt.checkpw(any(), any())).thenReturn(false)

        viewModel.login(username, password, url, false)

        val resultType = (viewModel.result.value as Result.Success).data
        assertEquals(ResultType.LoginInvalidCredentials, resultType)
    }

    @Test
    fun `login should result LoginOfflineUnsupported when not online but has network and not logged in before`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        `when`(NetworkUtils.isOnline()).thenReturn(false)
        `when`(NetworkUtils.hasNetwork()).thenReturn(true)
        `when`(OpenmrsAndroid.isUserLoggedOnline()).thenReturn(false)
        `when`(OpenmrsAndroid.getLastLoginServerUrl()).thenReturn(url)

        viewModel.login(username, password, url, false)

        val resultType = (viewModel.result.value as Result.Success).data
        assertEquals(ResultType.LoginOfflineUnsupported, resultType)
    }

    @Test
    fun `login should result LoginOfflineUnsupported when not online but has network and changes url`() {
        val username = "username123"
        val password = "password123"
        val url1 = initialUrl
        val url2 = "http://www.some_different_url.com"
        `when`(NetworkUtils.isOnline()).thenReturn(false)
        `when`(NetworkUtils.hasNetwork()).thenReturn(true)
        `when`(OpenmrsAndroid.isUserLoggedOnline()).thenReturn(true)
        `when`(OpenmrsAndroid.getLastLoginServerUrl()).thenReturn(url1)

        viewModel.login(username, password, url2, false)

        val resultType = (viewModel.result.value as Result.Success).data
        assertEquals(ResultType.LoginOfflineUnsupported, resultType)
    }

    @Test
    fun `login should result LoginNoInternetConnection when not online and has no network and not logged in before`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        `when`(NetworkUtils.isOnline()).thenReturn(false)
        `when`(NetworkUtils.hasNetwork()).thenReturn(false)
        `when`(OpenmrsAndroid.isUserLoggedOnline()).thenReturn(false)

        viewModel.login(username, password, url, false)

        val resultType = (viewModel.result.value as Result.Success).data
        assertEquals(ResultType.LoginNoInternetConnection, resultType)
    }

    @Test
    fun `login online should result LoginInvalidCredentials when not authenticated`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        val session = Session(sessionId = "123", isAuthenticated = false, user = null)

        `when`(NetworkUtils.isOnline()).thenReturn(true)
        `when`(loginRepository.getSession(username, password)).thenReturn(Observable.just(session))

        viewModel.login(username, password, url, false)

        val resultType = (viewModel.result.value as Result.Success).data
        assertEquals(ResultType.LoginInvalidCredentials, resultType)
    }

    @Test
    fun `login online should result LoginSuccess when succeeds`() {
        val username = "username123"
        val password = "password123"
        val url = initialUrl
        val session = Session(sessionId = "123", isAuthenticated = true, user = User())
        val visitType = VisitType()
        visitType.display = "Pharmacy"
        visitType.uuid = "uuid123"

        `when`(NetworkUtils.isOnline()).thenReturn(true)
        `when`(OpenmrsAndroid.getUsername()).thenReturn(username)
        `when`(OpenmrsAndroid.getServerUrl()).thenReturn(url)
        `when`(loginRepository.getSession(username, password)).thenReturn(Observable.just(session))
        `when`(visitRepository.getVisitType()).thenReturn(Observable.just(visitType))

        viewModel.login(username, password, url, false)

        val resultType = (viewModel.result.value as Result.Success).data
        assertEquals(ResultType.LoginSuccess, resultType)
    }

    @Test
    fun saveLocationsToDatabase() {
        val locations = listOf(LocationEntity("Pharmacy"), LocationEntity("Clinic"))
        val selectedLocation = "Pharmacy"
        `when`(locationDAO.deleteAllLocations()).thenReturn(Observable.just(true))
        `when`(locationDAO.saveLocation(any())).thenReturn(Observable.just(1L))

        viewModel.saveLocationsToDatabase(locations, selectedLocation)

        OpenmrsAndroidMock.verify { OpenmrsAndroid.setLocation(selectedLocation) }
        verify(locationDAO).deleteAllLocations()
        verify(locationDAO, times(1)).saveLocation(locations[0])
        verify(locationDAO, times(1)).saveLocation(locations[1])
    }

    @Test
    fun `fetchLocations online should result LocationsFetchingSuccess when succeeds`() {
        val url = "  http://www.some_different_url.com  "
        val trimmedUrl = "http://www.some_different_url.com"
        val locations = listOf(LocationEntity("Pharmacy"), LocationEntity("Clinic"))
        `when`(NetworkUtils.hasNetwork()).thenReturn(true)
        `when`(locationRepository.getLocations(url)).thenReturn(Observable.just(locations))
        val RestServiceBuilderMock = Mockito.mockStatic(RestServiceBuilder::class.java)

        viewModel.fetchLocations(url)
        val resultType = (viewModel.result.value as Result.Success).data

        RestServiceBuilderMock.verify { RestServiceBuilder.changeBaseUrl(trimmedUrl) }
        OpenmrsAndroidMock.verify { OpenmrsAndroid.setServerUrl(url) }
        assertEquals(url, viewModel.lastCorrectUrl)
        assertIterableEquals(locations, viewModel.locations)
        assertEquals(ResultType.LocationsFetchingSuccess, resultType)

        RestServiceBuilderMock.close()
    }

    @Test
    fun `fetchLocations online should result error LocationsFetching when fails`() {
        val url = initialUrl
        val throwable = Throwable("error")
        `when`(NetworkUtils.hasNetwork()).thenReturn(true)
        `when`(locationRepository.getLocations(url)).thenReturn(Observable.error(throwable))

        viewModel.fetchLocations(url)

        val operationType = (viewModel.result.value as Result.Error).operationType
        assertEquals(OperationType.LocationsFetching, operationType)
    }

    @Test
    fun `fetchLocations offline should result LocationsFetchingLocalSuccess when succeeds`() {
        val url = "http://www.some_different_url.com"
        val locations = listOf(LocationEntity("Pharmacy"), LocationEntity("Clinic"))
        `when`(NetworkUtils.hasNetwork()).thenReturn(false)
        `when`(locationDAO.getLocations()).thenReturn(Observable.just(locations))

        viewModel.fetchLocations(url)
        val resultType = (viewModel.result.value as Result.Success).data

        assertEquals(url, viewModel.lastCorrectUrl)
        assertIterableEquals(locations, viewModel.locations)
        assertEquals(ResultType.LocationsFetchingLocalSuccess, resultType)
    }

    @Test
    fun `fetchLocations offline should result LocationsFetchingNoInternetConnection when no locations found`() {
        val url = "http://www.some_different_url.com"
        val locations = emptyList<LocationEntity>()
        `when`(NetworkUtils.hasNetwork()).thenReturn(false)
        `when`(locationDAO.getLocations()).thenReturn(Observable.just(locations))

        viewModel.fetchLocations(url)
        val resultType = (viewModel.result.value as Result.Success).data

        assertIterableEquals(locations, viewModel.locations)
        assertEquals(ResultType.LocationsFetchingNoInternetConnection, resultType)
    }
}
