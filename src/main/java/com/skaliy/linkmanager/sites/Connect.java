package com.skaliy.linkmanager.sites;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class Connect {
    private String linkSite, linkInfo, title, linkIcon, iconName;
    private String[] info;
    private Document docSite, docInfo;
    private Elements elementsSiteLink;
    private File fileIcon;

    public Connect(String linkSite, String linkInfo, String elemClassInfo) {
        connect(linkSite);
        if (linkInfo != null)
            parseInfo(linkInfo, elemClassInfo);
    }

    private void connect(String linkSite) {
        try {
            this.linkSite = linkSite;
            try {
                docSite = Jsoup.connect(linkSite).userAgent("Chrome").get();
            } catch (HttpStatusException | SocketTimeoutException | ConnectException exception) {
                this.linkSite = "https://www.google.com.ua";
                docSite = Jsoup.connect(this.linkSite).userAgent("Chrome").get();
            }
            title = docSite.title();
            elementsSiteLink = docSite.select("link");
            linkIcon = parseIcon(elementsSiteLink);
            linkIcon = linkIcon.substring(0, linkIcon.lastIndexOf(".") + 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseInfo(String linkInfo, String elemClass) {
        this.linkInfo = linkInfo;
        info = new String[4];
        try {
            try {
                docInfo = Jsoup.connect(this.linkInfo).userAgent("Mozilla").get();
            } catch (HttpStatusException | SocketTimeoutException | ConnectException exception) {
                this.linkInfo = "https://ru.wikipedia.org/wiki/Google_(поисковая_система)";
                elemClass = "mw-parser-output";
                docInfo = Jsoup.connect(this.linkInfo).userAgent("Mozilla").get();
            }

            for (int i = 0; i <= 3; i++) {
                try {
                    info[i] = docInfo.body().getElementsByClass(elemClass).select("p").get(i).text() + "\n";
                } catch (IndexOutOfBoundsException e1) {
                    try {
                        info[i] = docInfo.body().getElementById(elemClass).select("p").get(i).text() + "\n";
                    } catch (NullPointerException e2) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseIcon(Elements elements) {
        for (Element element : elements)
            if (element.attr("rel").contains("shortcut icon")
                    && element.attr("href").contains(".png"))
                return element.attr("abs:href");
        for (Element element : elements)
            if (element.attr("rel").contains("icon")
                    && element.attr("href").contains(".png"))
                return element.attr("abs:href");
        for (Element element : elements)
            if (element.attr("rel").contains("apple-touch-icon")
                    && element.attr("href").contains(".png"))
                return element.attr("abs:href");
        for (Element element : elements)
            if (element.attr("rel").contains("shortcut icon"))
                return element.attr("abs:href");
        return "https://icon-icons.com/icons2/906/PNG/512/question-circular-button-1_icon-icons.com_69968.png";
    }

    /*private void downloadCacheIcon() {
        iconName = (title.contains(" ")
                ? title.substring(0, title.indexOf(" "))
                : title.contains(".")
                ? title.substring(0, title.indexOf("."))
                : title.contains("/")
                ? title.substring(0, title.indexOf("/"))
                : title)
                + linkIcon.substring(linkIcon.lastIndexOf("."));

        fileIcon = new File("src/cache/" + iconName);

        try {
            BufferedInputStream buf = new BufferedInputStream(new URL(linkIcon).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(fileIcon);
            byte[] b = new byte[1024];
            int count = 0;
            while ((count = buf.read(b)) != -1)
                fileOutputStream.write(b, 0, count);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public String getLinkSite() {
        return linkSite;
    }

    public void setLinkSite(String linkSite) {
        this.linkSite = linkSite;
    }

    public String getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(String linkInfo) {
        this.linkInfo = linkInfo;
    }

    public String[] getInfo() {
        return info;
    }

    public void setInfo(String[] info) {
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkIcon() {
        return linkIcon;
    }

    public void setLinkIcon(String linkIcon) {
        this.linkIcon = linkIcon;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public Document getDocSite() {
        return docSite;
    }

    public void setDocSite(Document docSite) {
        this.docSite = docSite;
    }

    public Document getDocInfo() {
        return docInfo;
    }

    public void setDocInfo(Document docInfo) {
        this.docInfo = docInfo;
    }

    public void setElementsSiteLink(Elements elementsSiteLink) {
        this.elementsSiteLink = elementsSiteLink;
    }

    public Elements getElementsSiteLink() {
        return elementsSiteLink;
    }

    public void setFileIcon(File fileIcon) {
        this.fileIcon = fileIcon;
    }

    public File getFileIcon() {
        return fileIcon;
    }
}
