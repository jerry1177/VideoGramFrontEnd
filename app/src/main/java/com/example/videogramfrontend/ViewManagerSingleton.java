package com.example.videogramfrontend;

/**
 * enum to keep track of what view is
 * curently being displayed or on top of the stack
 */
enum CurrentView {
    LOGIN,
    SIGNUP,
    HOME,
    SEARCH,
    UPLOAD
}

/**
 * enum to keep track of what view
 * the home fragment should navigate to because
 * findroute and addvehicle fragments just pop back to homeview
 */
enum ToView {
    LOGIN,
    SIGNUP,
    HOME,
    SEARCH,
    UPLOAD
}


/**
 * enum to keep track of what type the current user is
 */


public class ViewManagerSingleton {
    private static ViewManagerSingleton m_Singleton = null;
    private CurrentView currentView;
    private ToView toView;

    private ViewManagerSingleton() {
        currentView = CurrentView.LOGIN;
        toView = ToView.LOGIN;
    }

    public static synchronized ViewManagerSingleton GetSingleton() {
        if (m_Singleton == null)
            m_Singleton = new ViewManagerSingleton();

        return m_Singleton;
    }

    // Set methods
    public void setCurrentView(CurrentView currentView) { this.currentView = currentView; }
    public void setToView(ToView toView) { this.toView = toView; }

    public CurrentView getCurrentView() { return currentView; }
    public ToView getToView() { return toView; }
}
