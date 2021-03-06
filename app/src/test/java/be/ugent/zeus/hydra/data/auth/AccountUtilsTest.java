package be.ugent.zeus.hydra.data.auth;

import android.accounts.Account;
import android.accounts.AccountManager;

import be.ugent.zeus.hydra.BuildConfig;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAccountManager;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.threeten.bp.LocalDateTime;

import java.net.URLDecoder;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AccountUtilsTest {

    @Test
    public void hasAccount() {

        // Test that there are no accounts
        assertFalse(AccountUtils.hasAccount(RuntimeEnvironment.application));

        // Add a test account
        ShadowAccountManager manager = shadowOf(AccountManager.get(RuntimeEnvironment.application));
        Account testAccount = new Account("TEST", MinervaConfig.ACCOUNT_TYPE);
        manager.addAccount(testAccount);

        // Test the actual method
        assertTrue(AccountUtils.hasAccount(RuntimeEnvironment.application));
    }

    @Test
    public void getAccount() {
        ShadowAccountManager manager = shadowOf(AccountManager.get(RuntimeEnvironment.application));
        Account testAccount = new Account("TEST", MinervaConfig.ACCOUNT_TYPE);
        manager.addAccount(testAccount);

        assertEquals(testAccount, AccountUtils.getAccount(RuntimeEnvironment.application));
    }

    @Test
    public void getExpirationDate() {
        AccountManager accountManager = AccountManager.get(RuntimeEnvironment.application);
        ShadowAccountManager manager = shadowOf(accountManager);
        Account testAccount = new Account("TEST", MinervaConfig.ACCOUNT_TYPE);
        manager.addAccount(testAccount);

        LocalDateTime testValue = LocalDateTime.parse(
                LocalDateTime.now().format(MinervaAuthenticator.formatter), MinervaAuthenticator.formatter);
        accountManager
                .setUserData(testAccount, MinervaAuthenticator.EXP_DATE, testValue.format(MinervaAuthenticator.formatter));

        assertEquals(testValue, AccountUtils.getExpirationDate(accountManager, testAccount));
    }

    @Test
    public void getRequestUri() throws Exception {

        String url = AccountUtils.getRequestUri();

        // Do some testing, we don't test if the URL is exactly right, as we can't do that.
        assertThat(url, startsWith(MinervaConfig.AUTHORIZATION_ENDPOINT));

        UriComponents components = UriComponentsBuilder.fromHttpUrl(url).build();
        MultiValueMap<String, String> query = components.getQueryParams();

        assertEquals(BuildConfig.OAUTH_ID, query.getFirst(OAuth.OAUTH_CLIENT_ID));
        assertEquals(MinervaConfig.CALLBACK_URI, URLDecoder.decode(query.getFirst(OAuth.OAUTH_REDIRECT_URI), "UTF-8"));
        assertEquals(ResponseType.CODE.toString(), query.getFirst(OAuth.OAUTH_RESPONSE_TYPE));;
    }
}