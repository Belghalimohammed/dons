package com.isima.dons.configuration;

import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;

public class SessionUtils {

    public static void clearSessionExcept(HttpSession session, String excludeAttribute) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            if (!attributeName.equals(excludeAttribute)) {
                session.removeAttribute(attributeName);
            }
        }
    }
}
