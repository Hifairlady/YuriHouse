package com.edgar.yurihouse.Utils;

import android.os.Handler;
import android.os.Message;

import com.edgar.yurihouse.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by Edgar on 2018/4/9.
 */

public class HtmlParser extends Thread {

    private DataFromDocument dataFromDocument;

    private String queryURL;
    private Handler jsoupHandler;

    public HtmlParser(String queryURL, Handler jsoupHandler) {
        this.queryURL = queryURL;
        this.jsoupHandler = jsoupHandler;
    }

    @Override
    public void run() {
        super.run();
        Message message = Message.obtain();
        try {
            Document document = Jsoup.connect(queryURL).get();
            dataFromDocument.setData(document);
            message.what = R.integer.get_data_success;
            jsoupHandler.sendMessage(message);

        } catch (IOException e) {
            e.printStackTrace();
            message.what = R.integer.get_data_failed;
            message.obj = "Failed: IOException";
            jsoupHandler.sendMessage(message);

        } catch (NullPointerException npe) {
            npe.printStackTrace();
            message.what = R.integer.get_data_failed;
            message.obj = "Failed: NullPointerException";
            jsoupHandler.sendMessage(message);

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
            message.what = R.integer.get_data_failed;
            message.obj = "Failed: Illegal Url";
            jsoupHandler.sendMessage(message);

        }
    }

    public void setDataFromDocument(DataFromDocument dataFromDocument) {
        this.dataFromDocument = dataFromDocument;
    }

    public interface DataFromDocument {
        void setData(Document document);
    }

}
