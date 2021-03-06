package com.octopusbeach.wearforgit.helpers;

/**
 * Created by hudson on 6/23/15.
 */
public class AuthHelper {

    private static final String CLIENT_ID = "3a9274e027b36dabe965";
    private static final String CLIENT_SECRET = "410df2cfdbcd904db95d0cb7139b95110bf86fc2";
    public static final String STATE = "~.ADyVa0i26lKX";
    private static final String REPO = "repo";

    public static final String AUTH_URL = "https://github.com/login/oauth/authorize";
    public static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    public static final String REDIRECT = "http://octopusbeach.com/auth/github_oauth/callback";

    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";
    private static final String EQUALS = "=";

    public static final String STATE_PARAM = "state";

    public static final String TOKEN_KEY = "accessToken";
    public static final String USER_NAME_KEY = "userName";

    public static final String USER_URL = "https://api.github.com/user?access_token=";

    public static final String AVATAR_FILE_NAME = "avatar.png";

    // https://stackoverflow.com/questions/22062145/oauth-2-0-authorization-for-linkedin-in-android
    public static String getAuthorizationUrl() {
        return AUTH_URL
                + QUESTION_MARK + "client_id" + EQUALS + CLIENT_ID
                + AMPERSAND + "state" + EQUALS + STATE
                + AMPERSAND + "redirect_uri" + EQUALS + REDIRECT
                + AMPERSAND
                + "scope" + EQUALS + REPO;
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
