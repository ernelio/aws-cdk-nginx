package com.myorg;

import software.amazon.awscdk.core.App;


public class NginxCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new NginxCdkStack(app, "NginxCdkStack");

        app.synth();
    }
}
