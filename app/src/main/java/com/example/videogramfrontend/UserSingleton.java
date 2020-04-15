package com.example.videogramfrontend;

public class UserSingleton {

    // single instance
    private static UserSingleton m_instance = null;
    // member fields
    private int m_UserId;

    private UserSingleton() {
        m_UserId = 0;
    }

    public static synchronized UserSingleton getInstance() {
        if (m_instance == null)
            m_instance = new UserSingleton();

        return m_instance;
    }
    public int getUserId() { return m_UserId; }
    public void setUserId(int m_UserId) { this.m_UserId = m_UserId; }
}
