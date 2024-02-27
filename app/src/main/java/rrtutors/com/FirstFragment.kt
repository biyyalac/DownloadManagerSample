package rrtutors.com

import android.R
import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.IntentFilter
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import rrtutors.com.databinding.FragmentFirstBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
lateinit var downloadManager:DownloadManager
lateinit var request:Request
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        downloadManager=context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val url = "https://www.junkybooks.com/administrator/thebooks/659fbce6a082c-the-psychology-of-motivation.pdf"
        var fileName = url.substring(url.lastIndexOf('/') + 1)
        fileName = fileName.substring(0, 1).uppercase(Locale.getDefault()) + fileName.substring(1)
        val file: File = File(Environment.getDataDirectory(),fileName)

        request=Request(Uri.parse(url))
            .setTitle("Download")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            //.setDestinationUri(Uri.fromFile(file))// Uri of the destination file
            .setTitle("fileName")// Title of the Download Notification
            .setDescription("Downloading")// Description of the Download Notification
            .setRequiresCharging(false)// Set if charging is required to begin the download
            .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
            .setAllowedOverRoaming(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        binding.buttonFirst.setOnClickListener {
           // findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
           val dowloadValue= downloadManager.enqueue(request)
            Log.e("dowloadValue ","dowloadValue $dowloadValue")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.registerReceiver(DownloadReceiver(), IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),RECEIVER_NOT_EXPORTED)
            }


        }


        binding.buttonScnd.setOnClickListener {
            SendHttpRequestTask(requireContext()).execute()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
private class SendHttpRequestTask(val ctx:Context) : AsyncTask<String?, Void?, String?>() {


    override fun onPostExecute(data: String?) {

    }

    override fun doInBackground(vararg params: String?): String? {
        val url = "https://www.junkybooks.com/administrator/thebooks/659fbce6a082c-the-psychology-of-motivation.pdf"

        val baos = ByteArrayOutputStream()
        try {
            //println("URL [$url] - Name [$imgName]")

            val con = URL(url).openConnection() as HttpURLConnection
            con.requestMethod = "POST"
            con.doInput = true
            con.doOutput = true
            con.connect()
            //con.outputStream.write("name=$imgName".getBytes())

            val `is` = con.inputStream
            val b = ByteArray(1024)

            while (`is`.read(b) != -1) baos.write(b)

            con.disconnect()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return ""

}
}