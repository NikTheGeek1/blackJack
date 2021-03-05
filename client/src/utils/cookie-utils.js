export const USER_COOKIE = "BJ-user-logged-in";

export const storeUserCookie = (userEmail, userPassword) => {
    localStorage.setItem(USER_COOKIE, JSON.stringify({
        email: userEmail,
        password: userPassword,
        timeStamp: new Date(),
    }));
};

export const removeUserCookie = () => {
    localStorage.removeItem(USER_COOKIE);
};

export const doesCookieExists = cookieName => {
    return !!localStorage.getItem(cookieName);
};

export const getCookie = cookieName => {
    return JSON.parse(localStorage.getItem(cookieName));
};

export const isUserCookieValid = () => {
    if (!doesCookieExists(USER_COOKIE)) return false;
    const cookieTime = new Date(getCookie(USER_COOKIE).timeStamp);
    const timeDiff = Math.abs(cookieTime - new Date());
    return timeDiff < 10800000;
    // return timeDiff < 3000;
};