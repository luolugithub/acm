package com.xianghan.acm.ui;

import com.xianghan.acm.R;
import com.xianghan.acm.websocket.AppMessage;
import com.xianghan.acm.websocket.WebsocketService;
import com.xianghan.acm.websocket.WebsocketService.WSBinder;

import android.support.v7.app.ActionBarActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class AcmActivity extends ActionBarActivity {

	private static final boolean DEBUG = true;
	private static final String TAG = AcmActivity.class.getName();
	private static final int SHOW_PREFERENCES = 1;
	//private SharedPreferences mSharedPreferences;
	private WebsocketService mWebsocketService;
	
	
	public final static String KEY_PORT = "server_port";
	public final static String HTTP_SERVER = "server_ip";
	
	boolean mReconnect =false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acm);
        //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginButtonOnClickEventHandle();
        startService(new Intent(this,WebsocketService.class));
    }


    @Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this,WebsocketService.class), mWebsocketServiceConnection, Context.BIND_AUTO_CREATE);
	}


	@Override
	protected void onStop() {
		super.onStop();
		if(mWebsocketService!=null) {
			mWebsocketService.removeMessageHandler(mMessageHander);
			mWebsocketService.removeConnectionHandlers(mConnectionHander);
		}
		unbindService(mWebsocketServiceConnection);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.acm, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	Intent optionIntent = new Intent(this,OptionsActivity.class);
        	startActivityForResult(optionIntent, SHOW_PREFERENCES);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    private void loginButtonOnClickEventHandle(){
    	Button btnlogin = (Button)findViewById(R.id.butLogin);
        btnlogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText user = (EditText)AcmActivity.this.findViewById(R.id.editUserName);
				EditText pass = (EditText)AcmActivity.this.findViewById(R.id.editPassword);
				if(mWebsocketService!=null){
					mWebsocketService.login(user.getText().toString(), pass.getText().toString(), mReconnect);
				}
				
				
			}
		});
    }
    
    Handler mMessageHander = new Handler(){
    	@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			if(data.getInt("c") == AppMessage.LOGIN_SUCCESS){
				Intent i = new Intent(AcmActivity.this, PreviewActivity.class);
				startActivity(i);
			}
		}
    };
    
    Handler mConnectionHander = new Handler(){
    	
    };
    
    private ServiceConnection mWebsocketServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mWebsocketService = ((WSBinder)service).getService();
			mWebsocketService.addMessageHandler(mMessageHander);
			mWebsocketService.addConnectionHandlers(mConnectionHander);
		}
	};
	
	
}
