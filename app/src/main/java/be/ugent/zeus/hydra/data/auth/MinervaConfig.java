/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University Ghent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in all copies or
 *      substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package be.ugent.zeus.hydra.data.auth;

import be.ugent.zeus.hydra.data.network.requests.minerva.MinervaRequest;

/**
 * Various constants for Minerva accounts.
 *
 * The API endpoint is saved in the {@link MinervaRequest} for now.
 *
 * @author Niko Strijbol
 * @author kevin
 */
public class MinervaConfig {

    //Endpoints
    public final static String AUTHORIZATION_ENDPOINT = "https://oauth.ugent.be/authorize";
    public final static String TOKEN_ENDPOINT = "https://oauth.ugent.be/access_token";
    public final static String GRANT_INFORMATION_ENDPOINT = "https://oauth.ugent.be/tokeninfo";

    //The URL scheme of the callback
    public final static String CALLBACK_SCHEME = "hydra-ugent";
    public final static String CALLBACK_URI = "https://zeus.ugent.be/hydra/oauth/callback";

    //Account information
    public final static String ACCOUNT_TYPE = "be.ugent.zeus.hydra.minerva.account";
    public static final String ANNOUNCEMENT_AUTHORITY = "be.ugent.zeus.hydra.minerva.provider";
    public static final String CALENDAR_AUTHORITY = "be.ugent.zeus.hydra.minerva.calendar.provider";
    public static final String COURSE_AUTHORITY = "be.ugent.zeus.hydra.minerva.course.provider";

    //Scopes
    //These are currently not used.
    public final static String DEFAULT_SCOPE = "MINERVA_NOTIFICATION_COUNT";
    public static final String SCOPE_NOTIFICATION_COUNT = "MINERVA_NOTIFICATION_COUNT";
    public static final String SCOPE_COURSE_LIST = "MINERVA_COURSE_LIST";
    public static final String SCOPE_COURSE_CONTENT = "MINERVA_COURSE_CONTENT";

    //see https://github.ugent.be/Onderwijstechnologie/ugent-android-sdk/wiki/Registering-Your-Application
}