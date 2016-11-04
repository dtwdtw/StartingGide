package com.beabow.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.beabow.register.R;


public class WebActivity extends Activity{
	private Button back;
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WebActivity.this.finish();
				
			}
		});
		webView = (WebView) findViewById(R.id.webView);
		webView.setVisibility(WebView.VISIBLE);

        WebSettings ws = webView.getSettings();
        //ws.setUseWideViewPort(true);
        ws.setJavaScriptEnabled(true);
        //wv.addJavascriptInterface(new ContactsPlugin(), "contactsAction");
        //设置可以支持缩放   
        webView.getSettings().setSupportZoom(true);   
        //设置默认缩放方式尺寸是far   
        //webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);  

        //设置出现缩放工具   
        //webView.getSettings().setBuiltInZoomControls(true);
        // 让网页自适应屏幕宽度
        WebSettings webSettings= webView.getSettings(); // webView: 类WebView的实例
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        WebChromeClient wvcc = new WebChromeClient() {  
            @Override  
            public void onReceivedTitle(WebView view, String title) {  
                super.onReceivedTitle(view, title);  
                
            }  
  
        };  
        // 设置setWebChromeClient对象  
        webView.setWebChromeClient(wvcc);  
        webView.setDownloadListener(new DownloadListener (){

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Uri uri = Uri.parse(url);  
	            Intent intent = new Intent(Intent.ACTION_VIEW, uri);  
	            startActivity(intent);  
				
			}
        	
        });
        
        
        webView.setWebViewClient(new WebViewClient(){
        	public boolean shouldOverviewUrlLoading(WebView view,String url)  {
        		view.loadUrl(url);    
                return true;    
        		
        	}
        	
        	@Override
        	public void onPageStarted(WebView view, String url, Bitmap favicon) {
        		// TODO Auto-generated method stub
        		super.onPageStarted(view, url, favicon);
        	}
        	
        	 public void onPageFinished(WebView view, String url)   {
        		 super.onPageFinished(view, url);  
        		 //dismissWaitingDialog();
        	 }
        });
       // webView.loadUrl("content://com.android.htmlfileprovider/sdcard/index.html");
        webView.loadUrl(" file:///android_asset/web/privacypolicy.html ");   
	}
}
