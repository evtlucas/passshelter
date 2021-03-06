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

package br.unisinos.evertonlucas.passshelter.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.net.ConnectException;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.app.DefUserActivity;
import br.unisinos.evertonlucas.passshelter.app.DigCertActivity;
import br.unisinos.evertonlucas.passshelter.app.InstallState;
import br.unisinos.evertonlucas.passshelter.app.InstallStepFinished;
import br.unisinos.evertonlucas.passshelter.app.LoginActivity;
import br.unisinos.evertonlucas.passshelter.app.PassShelterApp;
import br.unisinos.evertonlucas.passshelter.data.ParseData;
import br.unisinos.evertonlucas.passshelter.model.CertificateBag;
import br.unisinos.evertonlucas.passshelter.model.ParseUser;
import br.unisinos.evertonlucas.passshelter.rep.GroupsRep;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.rep.ResourceRep;
import br.unisinos.evertonlucas.passshelter.rep.UserRep;
import br.unisinos.evertonlucas.passshelter.util.NetworkUtil;
import br.unisinos.evertonlucas.passshelter.util.ParseUtil;
import br.unisinos.evertonlucas.passshelter.util.ProgressDialogUtil;
import br.unisinos.evertonlucas.passshelter.util.SharedPrefUtil;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;
import br.unisinos.evertonlucas.passshelter.util.ToastUtil;

/**
 * Class designed for manage initialization process
 * Created by everton on 06/09/15.
 */
public class InitService implements InstallStepFinished, Serializable {

    private Context context;
    private final int versionCode;
    private final boolean firstInstallation;
    private boolean updateNeeded;

    public InitService(Context context, int versionCode, boolean firstInstallation) {
        this.context = context;
        this.versionCode = versionCode;
        this.firstInstallation = firstInstallation;
        this.updateNeeded = new UpdateService(this.context, this.versionCode, this.firstInstallation).isUpdateNeeded();
        ParseUtil.registerParse(this.context);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private InstallState getInstallState() {
        int state = SharedPrefUtil.readIntFrom(context, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_STATE);
        if (state == -1) state = 0;
        return InstallState.values()[state];
    }

    private void startActivity(Class clazz) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void initialize() {
        InstallState installState = getInstallState();
        if (this.updateNeeded) {
            UpdateService updateService = new UpdateService(this.context, this.versionCode, this.firstInstallation);
            if (updateService.isReinstallNeeded())
                installState = prepareUpdate();
            updateService.writeCurrentVersion();
        }
        if ((installState != InstallState.USER_CLOUD) && (installState != InstallState.READY))
            startActivityFromState(installState);
    }

    @NonNull
    private InstallState prepareUpdate() {
        new GroupsRep(this.context).deleteAll();
        new UserRep(this.context).deleteAll();
        new ResourceRep(this.context, null).deleteAll();
        InstallState installState = InstallState.INITIAL;
        persistState(installState);
        return installState;
    }

    private void sendUserToCloud() {
        KeyService keyService = PassShelterApp.getInstance().getKeyService();
        LocalUserRep localUserRep = PassShelterApp.createUserRep(this.context, keyService.getSymmetricEncryption());
        try {
            NetworkUtil.verifyNetwork(this.context);
            ProgressDialog dialog = ProgressDialogUtil.createProgressDialog(this.context, "Aguarde enquanto o usuário é salvo");
            String email = localUserRep.getUser();
            CertificateBag cert = keyService.getCertificateBag();
            ParseData parseData = new ParseData();
            ParseUser user = parseData.getExternalUser(email);
            if (user.isValid())
                parseData.updateUser(user, cert);
            else
                parseData.saveUser(email, cert);
            finished(InstallState.USER_CLOUD);
            dialog.dismiss();
        } catch (ConnectException e) {
            ToastUtil.showToastMessage(this.context, this.context.getString(R.string.msg_no_connection));
        } catch (Exception e) {
            persistState(InstallState.INITIAL);
            finished(InstallState.INITIAL);
            ShowLogExceptionUtil.showAndLogException(this.context, "Erro ao enviar dados para a nuvem", e);
        }
    }

    private void startActivityFromState(InstallState installState) {
        switch (installState) {
            case INITIAL:
                startActivity(DigCertActivity.class);
                break;
            case CERTIFICATE_INSTALLED:
                startActivity(DefUserActivity.class);
                break;
            case USER_DEFINED:
                sendUserToCloud();
                break;
            case USER_CLOUD: case READY:
                startLogin(installState);
                break;
        }
    }

    private void startLogin(InstallState installState) {
        Intent intent = new Intent(this.context, LoginActivity.class);
        if (installState == InstallState.USER_CLOUD) {
            intent.putExtra("init", false);
            persistState(InstallState.USER_CLOUD);
        } else {
            intent.putExtra("init", true);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    public void finished(InstallState state) {
        startActivityFromState(state);
    }

    @Override
    public void persistState(InstallState state) {
        // The context present in the class is not prepared to write in SharedPreferences
        SharedPrefUtil.writeIntTo(context, SharedPrefUtil.KEYCHAIN_PREF, SharedPrefUtil.KEYCHAIN_PREF_STATE, state.ordinal());
    }
}
