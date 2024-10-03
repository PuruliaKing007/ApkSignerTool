/*
 *  ApkSignerTool an Signer for Android Applications
 * Copyright 2024, PuruliaKing007
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of developer-krushna nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 */


package com.PuruliaCheatz.apksigner.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.PuruliaCheatz.apksigner.R;
import com.PuruliaCheatz.apksigner.utils.Signer;
import com.android.apksig.apk.ApkFormatException;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.net.Uri;

/*
Author @PuruliaKing007
*/

public class MainActivity extends Activity { 

    private EditText editText;
	private ImageView imageView;
    private Spinner spinner;
	private Spinner spinner2;
	private Button button;

	List<String> assetList;
	List<String> fileNameList = new ArrayList<>();
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		editText = (EditText) findViewById(R.id.activitymainEditText1);
		imageView  = (ImageView) findViewById(R.id.activitymainImageView1);
		spinner = (Spinner) findViewById(R.id.activitymainSpinner1);
		spinner2 = (Spinner) findViewById(R.id.activitymainSpinner2);
		button = (Button) findViewById(R.id.activitymainButton1);

		imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					apkImportDialog();
				}
			});

		try {
			final String[] S = getAssets().list("keys/");
		 	assetList = new ArrayList<String>(Arrays.asList(S));

			for (String file : assetList) {
				String fileName = file.split("\\.")[0];

				if (assetList.contains(fileName + ".pk8") && assetList.contains(fileName + ".x509.pem")) {
					if (!fileNameList.contains(fileName)) {
						fileNameList.add(fileName);
					}
				}
			}
		} catch (Exception e) {toast(e.toString());} 

		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, fileNameList);
		spinner.setAdapter(adapter);
		int index = fileNameList.indexOf("testkey");
		if (index != -1)
		    spinner.setSelection(index);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					Signer.signKeyName = fileNameList.get(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			
		ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sign_secheme));
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					switch (position) {
						case 0:
							Signer.setV1SigningEnabled = true;
							Signer.setV2SigningEnabled = true;
							Signer.setV3SigningEnabled = true;
							break;
						case 1:
							Signer.setV1SigningEnabled = true;
							Signer.setV2SigningEnabled = true;
							Signer.setV3SigningEnabled = false;
							break;
						case 2:
							Signer.setV1SigningEnabled = true;
							Signer.setV2SigningEnabled = false;
							Signer.setV3SigningEnabled = true;
							break;
						case 3:
							Signer.setV1SigningEnabled = true;
							Signer.setV2SigningEnabled = false;
							Signer.setV3SigningEnabled = false;
							break;
						case 4:
							Signer.setV1SigningEnabled = false;
							Signer.setV2SigningEnabled = true;
							Signer.setV3SigningEnabled = true;
							break;
						case 5:
							Signer.setV1SigningEnabled = false;
							Signer.setV2SigningEnabled = true;
							Signer.setV3SigningEnabled = false;
							break;
						case 6:
							Signer.setV1SigningEnabled = false;
							Signer.setV2SigningEnabled = false;
							Signer.setV3SigningEnabled = true;
							break;
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});

			
		button.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View p1) {
					if (!editText.getText().toString().isEmpty())
					    StartSign();
					else
						toast("Enter file path or select apk manualy");
				}
			});
			
			
    }

	private void apkImportDialog() {
		DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"apk", "APK"};
        FilePickerDialog  fpdialog = new FilePickerDialog(this, properties);
        fpdialog.setProperties(properties);
        fpdialog.setTitle("Select Apk");
        fpdialog.setPositiveBtnName("Select");
        fpdialog.setNegativeBtnName("Cancel");
        fpdialog.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    for (int i = 0; i < files.length; ++i) {
                        File file1 = new File(files[i]);
						if (file1.getName().endsWith(".apk") || file1.getName().endsWith(".APK")) {
							editText.setText(file1.getAbsolutePath());
						} else {
							toast("Error");
						}
                    }         
                }
            });
        fpdialog.show();      
    }
	
	@Override
    public boolean onCreateOptionsMenu (Menu menu){
        menu.add(0, 0, 0, "About");
        menu.add(0, 1, 1, "Exit");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 0:
				Intent var2 = new Intent("android.intent.action.VIEW");
				var2.setData(Uri.parse("https://t.me/PuruliaCheatz"));
				startActivity(var2);
                break;
            case 1:
				finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

	private long firstBackTime;

	@Override
	public void onBackPressed() {

		if (System.currentTimeMillis() - firstBackTime > 2000) {
			Toast.makeText(this, "Prees back once more", Toast.LENGTH_SHORT).show();
			firstBackTime = System.currentTimeMillis();
			return;
		}

		super.onBackPressed();
	}
	
	public void StartSign() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing...");
        progressDialog.setMessage("Your apk is almost ready...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        final @SuppressLint("HandlerLeak")
			Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                progressDialog.dismiss();
            }
        };

        new Thread() {
            public void run() {
                Looper.prepare();
				try {
					new Signer().calculateSignature(editText.getText().toString(), editText.getText().toString().replace(".apk", "_sign.apk"));
					dialogFinished();
					editText.setText("");
				} catch (ApkFormatException e) {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				} catch (GeneralSecurityException e) {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				}
				mHandler.sendEmptyMessage(0);
                Looper.loop();
			}
		}.start();
	}
	
	public void dialogFinished() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Signed!!");
        alertDialog.setMessage("Path: " + editText.getText().toString().replace(".apk", "_sign.apk"));
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Ok", null);
        alertDialog.show();
    }

	public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

} 

