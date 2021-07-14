package com.abuyork.tiktokvideodownloader
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.abuyork.tiktokvideodownloader.receiver.Receiver
import com.abuyork.tiktokvideodownloader.services.ClipboardMonitor
import com.abuyork.tiktokvideodownloader.tasks.downloadVideo
import com.abuyork.tiktokvideodownloader.utils.Constants.STARTFOREGROUND_ACTION
import com.abuyork.tiktokvideodownloader.utils.Constants.STOPFOREGROUND_ACTION
import com.abuyork.tiktokvideodownloader.utils.iUtils;
import com.abuyork.tiktokvideodownloader.utils.iUtils.isPackageInstalled
import kotlinx.android.synthetic.main.fragment_download.*
import kotlinx.android.synthetic.main.fragment_download.view.*

class download : Fragment() {
    private lateinit var mInterstitialAd: InterstitialAd
    private var NotifyID = 1001;

    private var csRunning=false;

   lateinit var prefEditor: SharedPreferences.Editor;
    lateinit var pref:SharedPreferences;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
         val view: View = inflater!!.inflate(R.layout.fragment_download, container, false)
        mInterstitialAd = InterstitialAd(context!!)
        mInterstitialAd.adUnitId = getString(R.string.AdmobInterstitial)
        mInterstitialAd.loadAd(AdRequest.Builder().build())



                pref = context!!.getSharedPreferences("tikVideoDownloader", 0); // 0 - for private mode
             prefEditor = pref.edit();
            csRunning=pref.getBoolean("csRunning",false)

        createNotificationChannel(requireActivity(), NotificationManagerCompat.IMPORTANCE_LOW,true,getString(R.string.app_name),"Tiktok Auto Download");

        view.btnDownload.setOnClickListener { view ->
            //Log.d("clicked","true")
           // view.btnDownload.visibility=View.GONE
           //  pbFetchingVideo.visibility=View.VISIBLE
            val url = etURL.text.toString();
            DownloadVideo(url);


        }


        view.llFacebook.setOnClickListener{ View ->

            if(isPackageInstalled(context!!,"com.facebook.katana")){

                try {
                    val uri = "facebook:/newsfeed"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    iUtils.ShowToast(context!!,"Error occurred!")
            }

            }else{
                iUtils.ShowToast(context!!,"Install facebook app")
                val appPackageName = "com.facebook.katana" // getPackageName() from Context or Activity object
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }


            }
        }

        view.llTikTok.setOnClickListener { view ->
            if(isPackageInstalled(context!!,"com.ss.android.ugc.trill")){

                try {
                    val uri = "http://vm.tiktok.com/"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.ss.android.ugc.trill")
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    iUtils.ShowToast(context!!,"Error occurred!")
                }

            }else{
                iUtils.ShowToast(context!!,"Install Tiktok app")
                val appPackageName = "com.ss.android.ugc.trill" // getPackageName() from Context or Activity object
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }


            }



        }
        view.llInstagram.setOnClickListener { view ->
            if(isPackageInstalled(context!!,"com.instagram.android")){

                try {
                    val uri = "https://www.instagram.com/"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.instagram.android")
                    startActivity(intent)
                }catch (e:ActivityNotFoundException){
                    iUtils.ShowToast(context!!,"Error occurred!")
                }

            }else{
                iUtils.ShowToast(context!!,"Install Instagaram app")
                val appPackageName = "com.instagram.android" // getPackageName() from Context or Activity object
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }


            }


        }



        view.ivLink.setOnClickListener(fun(view: View) {
            val clipBoardManager =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val primaryClipData = clipBoardManager.primaryClip
            val clip = primaryClipData.getItemAt(0).text.toString()

            etURL.text = Editable.Factory.getInstance().newEditable(clip)
            DownloadVideo(clip);
        })
        if(csRunning){
            view.chkAutoDownload.isChecked=true;
            startClipboardMonitor()
        }else{
            view.chkAutoDownload.isChecked=false;
            stopClipboardMonitor()
        }
        view.chkAutoDownload.setOnClickListener{view ->
           val checked = view.chkAutoDownload.isChecked;

            if(checked){
                Log.e("loged","testing checked!")
                startClipboardMonitor()
            }else{
                Log.e("loged","testing unchecked!")
                stopClipboardMonitor()
               // setNofication(false);
            }

        }
                    return  view;
    }
    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {
        // 1
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2
            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // 3
            val notificationManager: NotificationManager =
               context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.e("loged","Notificaion Channel Created!")
        }
    }
     private   fun  setNofication(b: Boolean) {

                if(b){
                    val channelId = "${context!!.packageName}-${context!!.getString(R.string.app_name)}"
                    val notificationBuilder = NotificationCompat.Builder(context!!, channelId).apply {
                        setSmallIcon(R.drawable.notification_template_icon_bg) // 3
                       // setStyle(NotificationCompat.)
                       setLargeIcon(
                           BitmapFactory.decodeResource(context!!.getResources(),
                                R.drawable.notification_template_icon_bg))
                        setContentTitle("Auto Download Service") // 4
                        setContentText("Copy the link of video to start download") // 5
                        setOngoing(true)
                         priority = NotificationCompat.PRIORITY_LOW // 7
                        setSound(null)
                        setOnlyAlertOnce(true)
                        setAutoCancel(false)
                        addAction(R.drawable.navigation_empty_icon,  "Stop", makePendingIntent("quit_action"))

                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        val pendingIntent = PendingIntent.getActivity(requireActivity(), 0, intent, 0)

                        setContentIntent(pendingIntent)
                    }
                    with(NotificationManagerCompat.from(requireActivity())) {
                        // notificationId is a unique int for each notification that you must define
                        notify(NotifyID, notificationBuilder.build())

                        Log.e("loged","testing notification notify!")


                    }




                }else{
                     NotificationManagerCompat.from(requireActivity()).cancel(NotifyID);
                 }
        }
    fun startClipboardMonitor() {
        prefEditor.putBoolean("csRunning",true);
        prefEditor.commit()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val service = requireActivity()!!.startForegroundService(
                Intent(
                    requireContext(),
                    ClipboardMonitor::class.java
                ).setAction(STARTFOREGROUND_ACTION)
            );
        }else{
             val service = requireActivity()!!.startService(
                Intent(
                    requireContext(),
                    ClipboardMonitor::class.java
                )
            );
        }

    }
    fun stopClipboardMonitor(){
        prefEditor.putBoolean("csRunning",false);
        prefEditor.commit()

            val service = requireActivity()!!.stopService(
                Intent(
                    requireContext(),
                    ClipboardMonitor::class.java
                ).setAction(STOPFOREGROUND_ACTION)
            );


    }
    fun makePendingIntent(name: String): PendingIntent {
        val intent = Intent(requireActivity(), Receiver::class.java)
        intent.setAction(name)
        return PendingIntent.getBroadcast(requireActivity(), 0, intent, 0)
    }

    fun DownloadVideo(url: String){

        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.")
        }


        if(url.equals("") && !!iUtils.checkURL(url)){
            iUtils.ShowToast(context!!,"Please Enter a valid URI")
        }else{
            downloadVideo.Start(context!!,url!!,false);

        }
    }
}
