package com.example.ramesh.phototext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Bundle;

import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {
	Button b1;
	ImageView imageView1;
	static final int REQUEST_IMAGE_CAPTURE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		b1=(Button)findViewById(R.id.button);
		imageView1=(ImageView)findViewById(R.id.imgPreview);

		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			Bitmap bmp =drawTextToBitmap(this,imageBitmap);

		}
	}
	public Bitmap drawTextToBitmap(Context mContext,Bitmap old) {
		try {
			String mText= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date());
			String fileName=mText+"jpg";
			File direct = new File(Environment.getExternalStorageDirectory() + "/DirName");

			if (!direct.exists()) {
				File wallpaperDirectory = new File("/sdcard/DirName/");
				wallpaperDirectory.mkdirs();
			}

			File file = new File(new File("/sdcard/DirName/"), fileName);
			if (file.exists()) {
				file.delete();
			}
			try {
				FileOutputStream out = new FileOutputStream(file);
				old.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Resources resources = mContext.getResources();
			float scale = resources.getDisplayMetrics().density;
			Bitmap bitmap = BitmapFactory.decodeFile(fileName);
			android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
			// set default bitmap config if none
			if(bitmapConfig == null) {
				bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
			}
			// resource bitmaps are imutable,
			// so we need to convert it to mutable one
			bitmap = bitmap.copy(bitmapConfig, true);

			Canvas canvas = new Canvas(bitmap);
			// new antialised Paint
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			// text color - #3D3D3D
			paint.setColor(Color.rgb(110,110, 110));
			// text size in pixels
			paint.setTextSize((int) (12 * scale));
			// text shadow
			paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

			// draw text to the Canvas center
			Rect bounds = new Rect();
			paint.getTextBounds(mText, 0, mText.length(), bounds);
			int x = (bitmap.getWidth() - bounds.width())/6;
			int y = (bitmap.getHeight() + bounds.height())/5;

			canvas.drawText(mText, x * scale, y * scale, paint);

			imageView1.setImageBitmap(bitmap);
		} catch (Exception e) {
			// TODO: handle exception

		}

	}
//	private void drawTextToBitmap(Context mcontext, int resourceid,Bitmap imageBitmap) {
//		try {
//			imageView1.setImageBitmap(imageBitmap);
//			Resources resources = mcontext.getResources();
//			float scale = resources.getDisplayMetrics().density;
//			Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceid);
//
//			android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
//			// set default bitmap config if none
//			if (bitmapConfig == null) {
//				bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
//			}
//			// resource bitmaps are imutable,
//			// so we need to convert it to mutable one
//			 final Bitmap bp1 = bitmap.copy(bitmapConfig, true);
//
//			Canvas canvas = new Canvas(bp1);
//			// new antialised Paint
//			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//			// text color - #3D3D3D
//			paint.setColor(Color.rgb(110, 110, 110));
//			// text size in pixels
//			paint.setTextSize((int) (12 * scale));
//			// text shadow
//			paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
//
//			// draw text to the Canvas center
//			Rect bounds = new Rect();
//			String dateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",
//					Locale.getDefault()).format(new Date());
//			paint.getTextBounds(dateTime, 0, dateTime.length(), bounds);
//			int x = (bitmap.getWidth() - bounds.width()) / 6;
//			int y = (bitmap.getHeight() + bounds.height()) / 5;
//
//			canvas.drawText(dateTime, x * scale, y * scale, paint);
//
//			imageView1.setImageBitmap(bp1);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
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

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}