package com.mapnamind.data.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileDownloadView implements Serializable {

    private List<DownloadView> downloads;

    public List<DownloadView> getDownloads() {
        return downloads;
    }

    public void setDownloads(DownloadView download) {
        if (downloads == null) {
            downloads = new ArrayList<>();
            downloads.add(download);
        } else {
            downloads.add(download);
        }
    }

    public static class DownloadView {

        private String topic;
        private String downloadPath;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getDownloadPath() {
            return downloadPath;
        }

        public void setDownloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
        }

    }

}
