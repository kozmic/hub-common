package com.blackducksoftware.integration.hub.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.exception.BDCIScopeException;

public enum Scope {
    /**
     * This scope indicates that the dependency is available in the classpath of the project. It is the default scope.
     */
    COMPILE,
    /**
     * This scope indicates that the dependency is not required for compilation, but is required during execution.
     */
    RUNTIME,
    /**
     * This scope indicates that the dependency is to be provided by JDK or web-Server/Container at runtime.
     */
    PROVIDED,
    /**
     * This scope indicates that you have to provide the system path.
     */
    SYSTEM,
    /**
     * This scope indicates that the dependency is only available for the test compilation and execution phases.
     */
    TEST,

    /**
     * This scope indicates that no scope was defined or the scope is unrecognized.
     */
    UNKNOWNSCOPE;

    public static Scope getScope(String scope) {
        if (scope.equalsIgnoreCase(COMPILE.toString())) {
            return Scope.COMPILE;
        } else if (scope.equalsIgnoreCase(RUNTIME.toString())) {
            return Scope.RUNTIME;
        } else if (scope.equalsIgnoreCase(PROVIDED.toString())) {
            return Scope.PROVIDED;
        } else if (scope.equalsIgnoreCase(SYSTEM.toString())) {
            return Scope.SYSTEM;
        } else if (scope.equalsIgnoreCase(TEST.toString())) {
            return Scope.TEST;
        } else {
            return Scope.UNKNOWNSCOPE;
        }
    }

    public static List<Scope> getScopeList() {
        List<Scope> scopeList = new ArrayList<Scope>();
        scopeList.add(COMPILE);
        scopeList.add(RUNTIME);
        scopeList.add(PROVIDED);
        scopeList.add(SYSTEM);
        scopeList.add(TEST);
        return scopeList;
    }

    public static List<String> getScopeStringList() {
        List<String> scopeList = new ArrayList<String>();
        scopeList.add(COMPILE.toString());
        scopeList.add(RUNTIME.toString());
        scopeList.add(PROVIDED.toString());
        scopeList.add(SYSTEM.toString());
        scopeList.add(TEST.toString());
        return scopeList;
    }

    public static List<String> getScopeListFromString(String userScopesToInclude) throws BDCIScopeException {
        List<String> scopesToInclude = new ArrayList<String>();
        String[] tokens = null;
        if (!StringUtils.isEmpty(userScopesToInclude)) {
            if (userScopesToInclude.contains(",")) {
                tokens = userScopesToInclude.split(",");
            } else {
                tokens = new String[1];
                tokens[0] = userScopesToInclude;
            }
            for (String scope : tokens) {
                if (Scope.getScope(scope.trim()).equals(Scope.UNKNOWNSCOPE)) {
                    throw new BDCIScopeException("The user has provided an unknown scope : " + scope);
                }
                scopesToInclude.add(Scope.getScope(scope.trim()).toString());
            }
        } else {
            throw new BDCIScopeException("Cannot get Scopes from an empty String");
        }
        return scopesToInclude;
    }
}