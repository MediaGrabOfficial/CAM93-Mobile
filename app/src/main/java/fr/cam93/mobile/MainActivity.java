package fr.cam93.mobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(6, 43, 89);
    private static final int YELLOW = Color.rgb(255, 213, 30);
    private static final int WHITE = Color.WHITE;
    private static final int TEXT = Color.rgb(31, 41, 55);
    private static final int MUTED = Color.rgb(98, 108, 122);
    private static final int LINE = Color.rgb(225, 229, 234);

    private static final String HOME = "https://cam93.org/";
    private static final String NEWS = "https://cam93.org/news/";
    private static final String SIGNUP = "https://cam93.org/inscription/";
    private static final String YOUNG_GROUPS = "https://cam93.org/inscription/groupes-jeunes/";
    private static final String SPECIALTIES = "https://cam93.org/inscription/groupes-de-specialites/";
    private static final String ATHLETES = "https://cam93.org/athletes/";
    private static final String RESULTS = "https://cam93.org/resultats-cam93/";
    private static final String RECORDS = "https://cam93.org/records-du-club/";
    private static final String HISTORY = "https://cam93.org/histoire-2/";
    private static final String STADIUMS = "https://cam93.org/installations/";
    private static final String RUNNING = "https://cam93.org/running/";
    private static final String MEMBERS = "https://monclub.eu/connexion/";
    private static final String COACHES = "https://cam93.monclub.app/";
    private static final String SHOP = "https://cam93.org/la-boutique-du-club/";
    private static final String MEETING = "https://www.meetingmontreuil.com/";
    private static final String PHONE = "0142872844";
    private static final String EMAIL = "contact@cam93.fr";
    private static final String MAPS = "https://www.google.com/maps/search/?api=1&query=21+rue+des+Grands+P%C3%AAchers+93100+Montreuil";
    private static final String LOGO_URL = "https://cam93.org/wp-content/uploads/2020/01/Logo_CAM93-300x263.png";

    private int currentTab = 0;
    private LinearLayout pageContent;
    private Bitmap cachedLogo;
    private boolean logoLoading = false;
    private Runner3DView runner3DView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
        render();
    }

    private void render() {
        if (runner3DView != null) {
            runner3DView.onPause();
            runner3DView = null;
        }
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets safe = insets.getInsets(WindowInsets.Type.systemBars());
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
                return insets;
            });
        }

        root.addView(createHeader(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(72)));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        pageContent = new LinearLayout(this);
        pageContent.setOrientation(LinearLayout.VERTICAL);
        pageContent.setPadding(dp(18), dp(14), dp(18), dp(24));
        scroll.addView(pageContent);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(createBottomNavigation(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(62)));

        setContentView(root);

        if (currentTab == 0) showHome();
        else if (currentTab == 1) showNews();
        else if (currentTab == 2) showClub();
        else showMenu();
    }

    private View createHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(14), dp(8), dp(16), dp(8));
        header.setBackgroundColor(WHITE);

        FrameLayout logoFrame = new FrameLayout(this);
        TextView fallback = text("CAM\n93", 12, YELLOW, true);
        fallback.setGravity(Gravity.CENTER);
        fallback.setBackground(rounded(NAVY, 4));
        logoFrame.addView(fallback, new FrameLayout.LayoutParams(dp(48), dp(48)));

        ImageView logo = new ImageView(this);
        logo.setContentDescription("Logo du CA Montreuil 93");
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logoFrame.addView(logo, new FrameLayout.LayoutParams(dp(48), dp(48)));
        loadClubLogoAsync(logo);

        header.addView(logoFrame, new LinearLayout.LayoutParams(dp(48), dp(48)));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        titles.setPadding(dp(11), 0, 0, 0);
        TextView title = text("CA MONTREUIL 93", 16, NAVY, true);
        TextView sub = text("ATHLÉTISME  ·  MONTREUIL", 10, MUTED, false);
        titles.addView(title);
        LinearLayout.LayoutParams subP = wrap();
        subP.topMargin = dp(2);
        titles.addView(sub, subP);
        header.addView(titles, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView site = text("cam93.org  ↗", 11, NAVY, true);
        site.setGravity(Gravity.CENTER);
        site.setPadding(dp(8), dp(7), dp(8), dp(7));
        site.setBackground(rounded(0xFFFFF6C7, 5));
        site.setOnClickListener(v -> openWeb(HOME, "Site officiel"));
        header.addView(site);

        View rule = new View(this);
        rule.setBackgroundColor(YELLOW);
        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.VERTICAL);
        outer.addView(header, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(71)));
        outer.addView(rule, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(3)));
        return outer;
    }

    private void loadClubLogoAsync(ImageView target) {
        if (cachedLogo != null) {
            target.setImageBitmap(cachedLogo);
            target.bringToFront();
            return;
        }
        if (logoLoading) return;
        logoLoading = true;
        new Thread(() -> {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(LOGO_URL).openConnection();
                connection.setConnectTimeout(4500);
                connection.setReadTimeout(4500);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("User-Agent", "CAM93-Mobile/1.1 Android");
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (Exception ignored) {
                // The text-based club mark remains available when offline.
            } finally {
                if (connection != null) connection.disconnect();
            }
            final Bitmap loaded = bitmap;
            runOnUiThread(() -> {
                logoLoading = false;
                if (loaded != null) {
                    cachedLogo = loaded;
                    target.setImageBitmap(loaded);
                    target.bringToFront();
                }
            });
        }).start();
    }

    private View createBottomNavigation() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setBackgroundColor(WHITE);
        nav.setElevation(dp(4));

        View line = new View(this);
        line.setBackgroundColor(LINE);

        String[] labels = {"Accueil", "Actualités", "Le club", "Menu"};
        String[] symbols = {"⌂", "N", "C", "≡"};
        LinearLayout holder = new LinearLayout(this);
        holder.setOrientation(LinearLayout.VERTICAL);
        holder.addView(line, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));
        holder.addView(nav, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(61)));

        for (int i = 0; i < labels.length; i++) {
            final int tab = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(2), dp(4), dp(2), dp(3));

            TextView icon = text(symbols[i], 18, currentTab == i ? NAVY : MUTED, true);
            icon.setGravity(Gravity.CENTER);
            TextView label = text(labels[i], 10, currentTab == i ? NAVY : MUTED, currentTab == i);
            label.setGravity(Gravity.CENTER);
            item.addView(icon);
            item.addView(label);
            if (currentTab == i) {
                View indicator = new View(this);
                indicator.setBackgroundColor(YELLOW);
                LinearLayout.LayoutParams indicatorP = new LinearLayout.LayoutParams(dp(25), dp(3));
                indicatorP.topMargin = dp(3);
                item.addView(indicator, indicatorP);
            } else {
                View spacer = new View(this);
                item.addView(spacer, new LinearLayout.LayoutParams(dp(25), dp(3)));
            }

            item.setOnClickListener(v -> {
                currentTab = tab;
                render();
            });
            nav.addView(item, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        }
        return holder;
    }

    private void showHome() {
        TextView eyebrow = text("BIENVENUE AU CLUB", 11, NAVY, true);
        pageContent.addView(eyebrow);
        addSpace(8);

        FrameLayout hero = new FrameLayout(this);
        hero.setBackground(rounded(NAVY, 7));
        hero.setClipToOutline(true);

        runner3DView = new Runner3DView(this);
        FrameLayout.LayoutParams modelParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        hero.addView(runner3DView, modelParams);

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setGravity(Gravity.CENTER_VERTICAL);
        copy.setPadding(dp(17), dp(15), 0, dp(15));

        TextView label = text("CA MONTREUIL 93", 10, YELLOW, true);
        copy.addView(label);

        TextView title = text("L’athlétisme\nqui avance.", 26, WHITE, true);
        title.setLineSpacing(0, 1f);
        LinearLayout.LayoutParams titleParams = wrap();
        titleParams.topMargin = dp(11);
        copy.addView(title, titleParams);

        TextView subtitle = text("Montreuil depuis 1943", 12, 0xFFE3EAF3, false);
        LinearLayout.LayoutParams subtitleParams = wrap();
        subtitleParams.topMargin = dp(9);
        copy.addView(subtitle, subtitleParams);

        TextView button = text("DÉCOUVRIR LE CLUB  →", 11, NAVY, true);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(11), dp(11), dp(11), dp(11));
        button.setBackground(rounded(YELLOW, 4));
        LinearLayout.LayoutParams buttonParams = wrap();
        buttonParams.topMargin = dp(17);
        copy.addView(button, buttonParams);
        button.setOnClickListener(v -> openWeb(HISTORY, "Histoire du club"));

        FrameLayout.LayoutParams copyParams = new FrameLayout.LayoutParams(
                dp(198), FrameLayout.LayoutParams.MATCH_PARENT, Gravity.START | Gravity.TOP);
        hero.addView(copy, copyParams);

        LinearLayout.LayoutParams heroParams = match();
        heroParams.height = dp(318);
        pageContent.addView(hero, heroParams);
        addSpace(15);

        addSectionTitle("À LA UNE");
        addRow("Les actualités du CAM93", "News, bilans et vie du club", NEWS);
        addRow("Les derniers résultats", "Performances des athlètes", RESULTS);
        addSectionTitle("COMPÉTITION");
        addRow("Résultats", "Compétitions et classements", RESULTS);
        addRow("Records du club", "Les performances de référence", RECORDS);
        addRow("Les athlètes", "Découvrir les athlètes du CAM93", ATHLETES);
        addSectionTitle("LE CLUB");
        addRow("Notre histoire", "Le club depuis 1943", HISTORY);
        addRow("Stades et installations", "Montreuil, Paris 19 et Bobigny", STADIUMS);
        addRow("La boutique du club", "Les équipements aux couleurs du CAM93", SHOP);
        addSectionTitle("PRATIQUER");
        addRow("Inscription en ligne", "Rejoindre le club ou renouveler sa licence", SIGNUP);
        addRow("Groupes jeunes", "École d’athlétisme et entraînements", YOUNG_GROUPS);
        addRow("Spécialités", "Trouver son groupe d’entraînement", SPECIALTIES);
        addRow("Running", "Entraînements et formules running", RUNNING);
        addSectionTitle("SERVICES");
        addRow("Espace adhérents", "Connexion Monclub", MEMBERS);
        addRow("Meeting de Montreuil", "Le rendez-vous international", MEETING);
        addContactSection();
        addFooter();
    }

    private void showNews() {
        addPageTitle("Actualités", "Les nouvelles et les temps forts du club.");
        addRow("Toutes les actualités", "Articles et newsletters du CAM93", NEWS);
        addRow("Résultats", "Consulter les performances des athlètes", RESULTS);
        addRow("Records", "Les records du club", RECORDS);
        addRow("Meeting de Montreuil", "Informations sur le meeting", MEETING);
        addSpace(18);
        addNotice("Les articles et les dates sont chargés depuis le site officiel afin de rester à jour.");
        addFooter();
    }

    private void showClub() {
        addPageTitle("Le club", "Une histoire montreuilloise commencée en 1943.");
        addRow("Histoire du CAM93", "Les grandes étapes du club", HISTORY);
        addRow("Athlètes", "Les camistes et leurs spécialités", ATHLETES);
        addRow("Stades et installations", "Les lieux d’entraînement", STADIUMS);
        addRow("Groupes jeunes", "École d’athlétisme", YOUNG_GROUPS);
        addRow("Spécialités", "Les différents groupes d’entraînement", SPECIALTIES);
        addRow("La boutique", "Équipements du club", SHOP);
        addRow("Meeting de Montreuil", "Site de l’événement", MEETING);
        addContactSection();
        addFooter();
    }

    private void showMenu() {
        addPageTitle("Menu", "Les rubriques et services du CA Montreuil 93.");
        addSectionTitle("COMPÉTITION");
        addRow("Résultats", "Résultats et classements", RESULTS);
        addRow("Records du club", "Records et performances", RECORDS);
        addRow("Athlètes", "Profils des athlètes", ATHLETES);
        addSectionTitle("ADHÉSION ET ENTRAÎNEMENT");
        addRow("Inscription en ligne", "Nouvelle inscription ou renouvellement", SIGNUP);
        addRow("Espace adhérents", "Connexion Monclub", MEMBERS);
        addRow("Espace coachs", "Accès réservé aux coachs", COACHES);
        addRow("Groupes jeunes", "École d’athlétisme", YOUNG_GROUPS);
        addRow("Spécialités", "Groupes et entraînements", SPECIALTIES);
        addRow("Running", "Offres et horaires running", RUNNING);
        addSectionTitle("INFORMATIONS");
        addRow("Actualités", "News du club", NEWS);
        addRow("Histoire", "Le CA Montreuil depuis 1943", HISTORY);
        addRow("Installations", "Les stades du CAM93", STADIUMS);
        addRow("Boutique", "Équipements et textiles", SHOP);
        addRow("Meeting de Montreuil", "Site de l’événement", MEETING);
        addRow("Site officiel", "cam93.org", HOME);
        addContactSection();
        addFooter();
    }

    private void addPageTitle(String title, String subtitle) {
        pageContent.addView(text(title, 27, NAVY, true));
        addSpace(4);
        pageContent.addView(text(subtitle, 13, MUTED, false));
        addSpace(10);
    }

    private void addSectionTitle(String title) {
        addSpace(11);
        TextView heading = text(title, 12, NAVY, true);
        heading.setPadding(0, dp(4), 0, dp(6));
        pageContent.addView(heading, wrap());
    }

    private void addRow(String title, String subtitle, String url) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(2), dp(12), dp(2), dp(12));
        row.setBackgroundResource(android.R.drawable.list_selector_background);

        LinearLayout textBlock = new LinearLayout(this);
        textBlock.setOrientation(LinearLayout.VERTICAL);
        textBlock.addView(text(title, 15, TEXT, true));
        if (subtitle != null && !subtitle.isEmpty()) {
            TextView detail = text(subtitle, 12, MUTED, false);
            LinearLayout.LayoutParams detailP = wrap();
            detailP.topMargin = dp(3);
            textBlock.addView(detail, detailP);
        }
        row.addView(textBlock, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView arrow = text("›", 24, NAVY, false);
        arrow.setGravity(Gravity.CENTER);
        row.addView(arrow, new LinearLayout.LayoutParams(dp(25), dp(36)));
        row.setOnClickListener(v -> openWeb(url, title));
        pageContent.addView(row, match());
        View separator = new View(this);
        separator.setBackgroundColor(LINE);
        LinearLayout.LayoutParams lineP = match();
        lineP.height = dp(1);
        pageContent.addView(separator, lineP);
    }

    private void addContactSection() {
        addSectionTitle("CONTACT");
        addRow("Appeler le club", "01 42 87 28 44", "tel:" + PHONE);
        addRow("Écrire au club", EMAIL, "mailto:" + EMAIL);
        addRow("Adresse du club", "21 rue des Grands Pêchers, 93100 Montreuil", MAPS);
    }

    private void addNotice(String message) {
        TextView note = text(message, 13, NAVY, false);
        note.setPadding(dp(12), dp(12), dp(12), dp(12));
        note.setBackground(rounded(0xFFFFF6C7, 5));
        pageContent.addView(note, match());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (runner3DView != null) runner3DView.onResume();
    }

    @Override
    protected void onPause() {
        if (runner3DView != null) runner3DView.onPause();
        super.onPause();
    }

    private void addFooter() {
        addSpace(22);
        View line = new View(this);
        line.setBackgroundColor(LINE);
        LinearLayout.LayoutParams lineP = match();
        lineP.height = dp(1);
        pageContent.addView(line, lineP);
        addSpace(10);
        TextView footer = text("CAM93 MOBILE  ·  Application non officielle\nContenus et services : cam93.org", 10, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        footer.setLineSpacing(dp(2), 1f);
        pageContent.addView(footer, match());
        addSpace(8);
    }

    private void openWeb(String url, String title) {
        try {
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if ("tel".equalsIgnoreCase(scheme) || "mailto".equalsIgnoreCase(scheme)) {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                return;
            }
            Intent intent = new Intent(this, WebContentActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Impossible d’ouvrir ce lien.", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView text(String value, int sizeSp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setFontFeatureSettings("kern");
        tv.setTypeface(Typeface.create("sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        tv.setIncludeFontPadding(true);
        return tv;
    }

    private GradientDrawable rounded(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private LinearLayout.LayoutParams match() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams wrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void addSpace(int heightDp) {
        View space = new View(this);
        pageContent.addView(space, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(heightDp)));
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
