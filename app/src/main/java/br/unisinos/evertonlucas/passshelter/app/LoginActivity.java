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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

import br.unisinos.evertonlucas.passshelter.R;
import br.unisinos.evertonlucas.passshelter.async.UpdateStatus;
import br.unisinos.evertonlucas.passshelter.rep.LocalUserRep;
import br.unisinos.evertonlucas.passshelter.service.InitService;
import br.unisinos.evertonlucas.passshelter.service.KeyService;
import br.unisinos.evertonlucas.passshelter.service.LoginService;
import br.unisinos.evertonlucas.passshelter.util.ShowLogExceptionUtil;

public class LoginActivity extends AppCompatActivity implements UpdateStatus {

    private LocalUserRep localUserRep;
    private EditText txtLoginPassword;
    private InitService initService;
    private KeyService keyService;
    private LoginService loginService;
    private TextView txtLoginMessage;
    private Button btnDefUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.txtLoginPassword = (EditText) findViewById(R.id.txtLoginPassword);
        this.txtLoginMessage = (TextView) findViewById(R.id.txt_login_messages);
        this.btnDefUser = (Button) findViewById(R.id.btnDefUser);

        this.initService = PassShelterApp.build(this).getInitService();
        this.keyService = PassShelterApp.createKeyService(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            setTxtLoginMessage("Aguarde a leitura do Certificado Digital");
            btnDefUser.setEnabled(false);
            this.keyService.loadCertificate();
        } catch (Exception e) {
            ShowLogExceptionUtil.showAndLogException(this, "Erro ao iniciar tela de cadastro do usuário", e);
        }
    }

    private void setTxtLoginMessage(String message) {
        this.txtLoginMessage.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);*/
        return false;
    }

    public void btnLogin(View view) {
        try {
            if (!this.loginService.login(this.txtLoginPassword.getText().toString())) {
                Toast.makeText(this, "Senha errada", Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
        } catch (NoSuchAlgorithmException e) {
            ShowLogExceptionUtil.showAndLogException(this, "Exceção ao salvar usuário", e);
        }
    }

    @Override
    public void update(boolean status) {
        setTxtLoginMessage("");
        btnDefUser.setEnabled(true);
        this.initService.setContext(this);
        this.localUserRep = PassShelterApp.createUserRep(this, keyService.getSymmetricEncryption());
        this.loginService = new LoginService(this, this.localUserRep, this.initService);
    }
}
