package br.unisinos.evertonlucas.passshelter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import br.unisinos.evertonlucas.passshelter.async.UpdateCertificateStatus;
import br.unisinos.evertonlucas.passshelter.bizserv.KeyService;
import br.unisinos.evertonlucas.passshelter.data.ExportSecretKeyData;
import br.unisinos.evertonlucas.passshelter.util.ConfirmationDialog;


public class Main extends ActionBarActivity implements UpdateCertificateStatus,
        ConfirmationDialog.ConfirmationDialogListener{

    private Button button;
    private KeyService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.service = new KeyService(this, this);
    }

    @Override
    protected void onResume() {
        this.service.reReadCertificate();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (id) {
            case R.id.action_backup:
                try {
                    service.exportCryptographicKey();
                } catch (Exception e) {
                    ExportSecretKeyData.throwException(e, this);
                }
                return true;
            case R.id.action_restore:
                service.importCryptographicKey();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnInstallCertificate(View v) {
        service.installCertificate();
    }

    @Override
    public void update(boolean status) {
        ImageView imgView = (ImageView) findViewById(R.id.imgStatusDigCert);
        if (status) {
            imgView.setImageResource(R.drawable.ic_thumb_up);
        } else {
            imgView.setImageResource(R.drawable.ic_thumb_down);
        }
    }

    @Override
    public void onConfirmationPositive() {
        Toast.makeText(this, "Clicado", Toast.LENGTH_LONG).show();
    }
}