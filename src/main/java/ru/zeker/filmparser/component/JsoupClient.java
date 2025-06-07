package ru.zeker.filmparser.component;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsoupClient {

    @Value("${parser.timeout.ms}")
    private Integer timeout;

    public Document fetchDocument(String url) throws IOException {
        Connection connection = Jsoup.connect(url)
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("accept-language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("cache-control", "max-age=0")
                .header("priority", "u=0, i")
                .header("referer", "https://sso.kinopoisk.ru/")
                .header("sec-ch-ua", "\"Google Chrome\";v=\"137\", \"Chromium\";v=\"137\", \"Not/A)Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("sec-fetch-dest", "document")
                .header("sec-fetch-mode", "navigate")
                .header("sec-fetch-site", "same-origin")
                .header("sec-fetch-user", "?1")
                .header("upgrade-insecure-requests", "1")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36")
                .cookie("i", "q8CrdxbodrWJXwL517ourkLUKpXAWxCX/hyEe2uCh6tU3dnoORThmVpFVKlTZO5G3/TSo2wl+gczrk4f7d5Ybm+wcXI")
                .cookie("yandexuid", "4615606151749152171")
                .cookie("yashr", "2756359541749152171")
                .cookie("gdpr", "0")
                .cookie("_ym_uid", "1749152171155267373")
                .cookie("bh", "EkEiR29vZ2xlIENocm9tZSI7dj0iMTM3IiwgIkNocm9taXVtIjt2PSIxMzciLCAiTm90L0EpQnJhbmQiO3Y9IjI0IioCPzA6CSJXaW5kb3dzImCs44fCBmoe3Mrh/wiS2KGxA5/P4eoD+/rw5w3r//32D8LzzIcI")
                .cookie("_ym_visorc", "b")
                .cookie("spravka", "dD0xNzQ5MTUyMTc2O2k9OTQuMjMxLjEzMi44MjtEPTg5QkU1Q0VCRjNFMDM5MjY2MDM3RTY2MEVGRjUwNUIxMkE2MTNGMUFEODRCMDBGNUE5NUZDRkM3MzdFMzE0RTUyOTEwMzExREUwOUJGMjM0NzQzNzZERTc0QjRGNTI1Q0M1MEYzQTFEQjQ1Q0EyOTAxMUE0QjBCRTUwMjE0NDBGNDBBQTYzRDEyODJDODExNjFFRTY1MzgzQzgzRUEzQkZFRTNCODQwRjY4OTU3OEI0OTA1OEEzRUMyOTgwNjBEMTI3O3U9MTc0OTE1MjE3NjQ5MjAzNjAzMztoPWI1OTY0ZGViMTJkMGIyMzc4MDQ1ZGMzMDkzYWI3M2E1")
                .cookie("_yasc", "W4libG8hHV52d4d1/8G3CiuZbLsJQfFrZkHiE80huMrIYI2vGMpX5WEnVWt7EKZy/+QxVEx3pA")
                .cookie("_csrf", "26hY-rGV5vhQCZmrCAmrz_o1")
                .cookie("disable_server_sso_redirect", "1")
                .cookie("ya_sess_id", "noauth:1749152196")
                .cookie("sessar", "1.1202.CiDxRRzAJFCqCTqiurdMuRvOqe9FnLcHuadwpnLjUpRSKQ.VGJPCMngKON8XxNn6NzCoOxqe19dMa64vvuNGWlMhHY")
                .cookie("yandex_login", "")
                .cookie("ys", "c_chck.2762450333")
                .cookie("mda2_beacon", "1749152196414")
                .cookie("sso_status", "sso.passport.yandex.ru:synchronized")
                .cookie("no-re-reg-required", "1")
                .cookie("_ym_isad", "1")
                .cookie("desktop_session_key", "052e70943d4a4739481c793e270dd55e399608034eea1e5c4b60ca2ab528fef91a75cf5944aadc6e73dc0533e0e093434d025122d6c35da6f0f63830a1d0fe06842686c861057c7e46c4708238e1664f4f97b86bc30a2524b8957a8a202dbf1f99a76982ec6fea81f06232e92e346efd")
                .cookie("desktop_session_key.sig", "-_XkecoTIgemYfwwuwqB2GEHigg")
                .cookie("_ym_d", "1749152198")
                .cookie("crookie", "TV7FIDaFSYqW6Cb9z8Fzga4u8+Lfh4aWnry8OJyzgLSwCgr5/aMjhfRcmVEIvrplySK3jrHvc3k15TNQm8UZjO/rvyg")
                .cookie("cmtchd", "MTc0OTE1MjE5ODgwNw")
                .cookie("location", "1")
                .cookie("coockoos", "1")
                .method(org.jsoup.Connection.Method.GET)
                .timeout(timeout)
                .ignoreContentType(true);

        return connection.get();
    }
}