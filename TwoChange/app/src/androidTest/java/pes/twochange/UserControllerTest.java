package pes.twochange;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pes.twochange.domain.controller.UserController;
import pes.twochange.domain.model.User;

import static junit.framework.Assert.fail;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UserControllerTest {
    private static UserController userController;

    @BeforeClass
    public static void setUp() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        userController = UserController.getInstance(appContext);
    }

    @Test
    public void registerSucceedsWithValidData() throws Exception {

    }

    @Test
    public void registerFailsWithInvalidData() {

    }

    @Test
    public void loginSucceedsWithValidUser() {
        userController.login("test@domain.com", "12345678", new UserController.OnLogin() {
            @Override
            public void onLoginSuccess(User user) {
                // OK
            }

            @Override
            public void onLoginFailure(String message) {
                fail("Login fails with a valid user with message: " + message);
            }
        });
    }

    @Test
    public void loginFailsWithInvalidUser() {
        userController.login("non-existent-user@fake.com", "thisisnotavalidpassword", new UserController.OnLogin() {
            @Override
            public void onLoginSuccess(User user) {
                fail("Login succeeds with an invalid user");
            }

            @Override
            public void onLoginFailure(String message) {
                // OK
            }
        });
    }
}
