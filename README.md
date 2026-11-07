# SMS-App (Android, Kotlin)

Bu ilkin versiya telefonun sahibinin razılığı ilə yeni gələn SMS bildirişlərini **yalnız həmin telefonda** göstərir.

- SMS qəbul etmə icazəsi tətbiqin içində istifadəçidən ayrıca istənir.
- Köhnə SMS-ləri oxumur; icazədən sonra gələn SMS-ləri göstərir.
- Mesajları yalnız cihazın daxili tətbiq yaddaşında saxlayır (son 50 SMS).
- İnternetə və heç bir serverə SMS ötürmür.
- Android 8.0+ üçün hazırlanıb.
- Google Play mağazasına yükləmək üçün SMS icazələri xüsusi siyasətlə məhdudlaşdırılır.

## Codemagic

Bu repository-nin kökündə `codemagic.yaml` var. Codemagic-də Native Android layihəsi kimi əlavə edin, `android-debug` workflow-unu başladın. Build nəticəsində `app-debug.apk` faylını **Artifacts** bölməsindən endirin.

`codemagic.yaml` build zamanı rəsmi Gradle 8.13 wrapper JAR-ını HTTPS üzərindən endirir və SHA-256 ilə təsdiqləyir. Paketə şübhəli binary daxil edilməyib.

## Qurma

1. ZIP-i kompüterdə açın; `SMS-App` qovluğunun **içindəkiləri** GitHub repository-sinə yükləyin. ZIP faylını təkbaşına GitHub-a qoymayın.
2. Codemagic layihəni GitHub-dan əlavə edir, `codemagic.yaml` avtomatik aşkarlanır.
3. `Start new build` > `android-debug` seçin.
4. APK-ni telefona yükləyib açın və SMS icazəsini verin.
5. Yeni SMS gəldikdə tətbiqdə görünəcək.

Bu layihə **vebsayta SMS ötürmür**; təhlükəsiz yerli funksionallıq üçün başlanğıcdır.
