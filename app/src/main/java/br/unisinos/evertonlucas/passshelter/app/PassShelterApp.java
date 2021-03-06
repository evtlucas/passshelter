/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package br.unisinos.evertonlucas.passshelter.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;

import java.io.Serializable;

import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.encryption.SymmetricEncryption;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.service.InitService;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;

/**
 * Class designed for control the application start process
 * Created by everton on 05/09/15.
 */
public class PassShelterApp implements Serializable {

    private InitService initService = null;
    private static PassShelterApp singleton = null;
    private KeyService keyService = null;
    private LocalUserRep localUserRepository = null;
    private static String localUser = "";
    /*public static GoogleAnalytics analytics;
    public static Tracker tracker;*/


    public PassShelterApp(Context context) {
        singleton = this;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            boolean firstInstallation = Math.abs(System.currentTimeMillis() - packageInfo.firstInstallTime) < 20000;
            initalizeGoogleAnalytics(context);
            initService = new InitService(context, versionCode, firstInstallation);
            initService.initialize();
        } catch (Exception e) {
            ShowLogExceptionUtil.logException(context, "Erro ao inicializar Pass Shelter", e);
        }
    }

    public static void initalizeGoogleAnalytics(Context context) {
        /*AnalyticsTrackers.initialize(context);

        analytics = GoogleAnalytics.getInstance(context);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-68582236-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);*/
    }

    public static PassShelterApp getInstance() {
        return singleton;
    }

    public static PassShelterApp build(Context context) {
        if (singleton == null)
            new PassShelterApp(context);
        return singleton;
    }

    public InitService getInitService() {
        return initService;
    }

    public KeyService getKeyService() {
        return keyService;
    }

    public static KeyService createKeyService(UpdateStatus status, Activity activity) {
        if (getInstance().keyService == null) {
            getInstance().keyService = new KeyService(status, activity);
        } else {
            getInstance().keyService.setUpdateCertificateStatus(status);
            getInstance().keyService.setActivity(activity);
        }
        return getInstance().keyService;
    }

    public static LocalUserRep createUserRep(Context context, SymmetricEncryption symmetricEncryption) {
        if (getInstance().localUserRepository == null) {
            getInstance().localUserRepository = new LocalUserRep(context, symmetricEncryption);
        } else {
            getInstance().localUserRepository.setContext(context);
        }
        try {
            localUser = getInstance().localUserRepository.getUser();
        } catch (Exception e) {
        }
        return getInstance().localUserRepository;
    }

    public static String getLocalUser() {
        return localUser;
    }
}
