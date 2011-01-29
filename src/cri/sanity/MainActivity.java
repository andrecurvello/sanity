package cri.sanity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;


public class MainActivity extends ActivityScreen
{
	@Override
  public void onCreate(Bundle savedInstanceState)
  {
		screener(MainActivity.class, R.xml.prefs);
    super.onCreate(savedInstanceState);
    screenerAll();
  	setupProximity();
    setupDonate();
		if(!A.is(K.AGREE))   askLicense();
		else if(P.upgrade()) alertChangeLog();
		else if(!A.isFull()) askDonate();
  }

	@Override
	public void onStart()
	{
		updateOptions();
		super.onStart();
	}

	//---- private api

	private void setupProximity()
	{
  	if(Dev.sensorProxim() == null)
  		setEnabled(K.SCREEN_PROXIMITY, false);
	}

	private void setupDonate()
	{
		Preference p = pref("donate");
    if(!A.isFull() && !startDonateApp())
   		on(p, new Click(){ boolean on(){ return A.gotoMarketDetails(Conf.DONATE_PKG); }});
    else {
	  	p.setEnabled(false);
	   	p.setTitle(A.FULL? R.string.full_title : R.string.donated_title);
	   	p.setSummary(A.FULL? R.string.full_sum : R.string.donated_sum);
			p = pref(K.SCREEN_RECORD);
			p.setSummary(p.getSummary()+" "+A.tr(R.string.rec_sum_free));
    }
	}

	private static void askDonate()
	{
		A.alert(
			A.tr(R.string.msg_donate),
			new A.Click(){ void on(){ A.gotoMarketDetails(Conf.DONATE_PKG); }},
			null
		);
	}

	private void askLicense()
	{
		A.alert(
		  A.tr(R.string.msg_eula_title),
			A.fullName()+"\n\n"+A.tr(R.string.app_desc)+"\n\n"+A.tr(R.string.msg_eula),
			new A.Click(){ void on(){ A.put(K.AGREE,true); P.setDefaults(); updateOptions(); }},
			new A.Click(){ void on(){ finish(); }},
			A.ALERT_OKCANC,
			false
		);
	}
	
	private void updateOptions()
	{
		final boolean enabled = A.isEnabled();
		setEnabled(K.SCREEN_DEVICES  , enabled);
		setEnabled(K.SCREEN_PROXIMITY, enabled);
		setEnabled(K.SCREEN_SPEAKER  , enabled);
		setEnabled(K.SCREEN_VOLUME   , enabled);
		setEnabled(K.SCREEN_NOTIFY   , enabled);
	}

	private boolean startDonateApp()
	{
		final boolean done = startService(new Intent(Conf.ACTION_DONATE)) != null;
		if(done) A.setFull();
		return done;
	}

}
