package com.octopusbeach.wearforgit;

/**
 * Created by hudson on 6/23/15.
 */
public class AuthHelper {

    private static final String CLIENT_ID = "3a9274e027b36dabe965";
    private static final String CLIENT_SECRET = "410df2cfdbcd904db95d0cb7139b95110bf86fc2";
    public static final String STATE = "~.ADyVa0i26lKX";

    public static final String AUTH_URL = "https://github.com/login/oauth/authorize";
    public static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    public static final String REDIRECT = "http://octopusbeach.com/auth/github_oauth/callback";

    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    public static final String STATE_PARAM = "state";

    // https://stackoverflow.com/questions/22062145/oauth-2-0-authorization-for-linkedin-in-android
    public static String getAuthorizationUrl() {
        return AUTH_URL
                + QUESTION_MARK + "client_id" + EQUALS + CLIENT_ID
                + AMPERSAND + "state" + EQUALS + STATE
                + AMPERSAND + "redirect_uri" + EQUALS + REDIRECT;
    }

    public static String getAccessTokenUrl(String authorizationToken) {
        return TOKEN_URL
                + QUESTION_MARK
                + "code" + EQUALS + authorizationToken
                + AMPERSAND
                + "client_id" + EQUALS + CLIENT_ID
                + AMPERSAND
                + "redirect_uri" + EQUALS + REDIRECT
                + AMPERSAND
                + "client_secret" + EQUALS + CLIENT_SECRET;
    }
}
