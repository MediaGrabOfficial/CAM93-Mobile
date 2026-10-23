package fr.cam93.mobile;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(6, 43, 89);
    private static final int YELLOW = Color.rgb(255, 213, 30);
    private static final int BACKGROUND = Color.rgb(244, 246, 249);
    private static final int TEXT = Color.rgb(22, 35, 56);
    private static final int MUTED = Color.rgb(102, 112, 133);
    private static final int WHITE = Color.WHITE;

    private LinearLayout root;
    private LinearLayout pageContent;
    private int currentTab = 0;

    private static final String HOME = "https://cam93.org/";
    private static final String NEWS = "https://cam93.org/news/";
    private static final String SIGNUP = "https://cam93.org/inscription/inscription-en-ligne/";
    private static final String ATHLETES = "https://cam93.org/athletes/";
    private static final String RESULTS = "https://cam93.org/resultats-cam93/";
    private static final String RECORDS = "https://cam93.org/records-du-club/";
    private static final String HISTORY = "https://cam93.org/histoire-2/";
    private static final String STADIUMS = "https://cam93.org/installations/";
    private static final String RUNNING = "https://cam93.org/running/";
    private static final String MEMBERS = "https://cam93.monclub.app/";
    private static final String MEETING = "https://www.meetingmontreuil.com/";
    private static final String PHONE = "0142872844";
    private static final String EMAIL = "contact@cam93.fr";
    private static final String ADDRESS = "21 rue des Grands Pêchers, 93100 Montreuil";

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
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BACKGROUND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets safe = insets.getInsets(WindowInsets.Type.systemBars());
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
                return insets;
            });
        }

        root.addView(createHeader(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(76)));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        pageContent = new LinearLayout(this);
        pageContent.setOrientation(LinearLayout.VERTICAL);
        pageContent.setPadding(dp(16), dp(18), dp(16), dp(26));
        scrollView.addView(pageContent);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(createBottomNavigation(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(66)));

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
        header.setPadding(dp(18), dp(8), dp(18), dp(8));
        header.setBackgroundColor(YELLOW);

        TextView badge = text("CAM\n93", 19, NAVY, true);
        badge.setGravity(Gravity.CENTER);
        badge.setLineSpacing(-dp(4), 1f);
        GradientDrawable badgeBg = rounded(NAVY, 12);
        badge.setBackground(badgeBg);
        badge.setTextColor(YELLOW);
        badge.setPadding(dp(10), dp(4), dp(10), dp(4));
        header.addView(badge, new LinearLayout.LayoutParams(dp(56), dp(54)));

        LinearLayout names = new LinearLayout(this);
        names.setOrientation(LinearLayout.VERTICAL);
        names.setPadding(dp(12), 0, 0, 0);
        TextView title = text("CAMONTREUIL 93", 18, NAVY, true);
        TextView subtitle = text("CLUB ATHLÉTIQUE • DEPUIS 1943", 10, NAVY, true);
        names.addView(title);
        names.addView(subtitle);
        header.addView(names, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView site = text("↗", 24, NAVY, true);
        site.setGravity(Gravity.CENTER);
        site.setContentDescription("Ouvrir le site officiel");
        site.setPadding(dp(8), 0, dp(4), 0);
        site.setOnClickListener(v -> openWeb(HOME, "Site officiel CAM93"));
        header.addView(site, new LinearLayout.LayoutParams(dp(42), dp(48)));
        return header;
    }

    private View createBottomNavigation() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setBackgroundColor(WHITE);
        nav.setElevation(dp(8));

        String[] labels = {"Accueil", "Actualités", "Le club", "Menu"};
        String[] icons = {"⌂", "▤", "🏟", "☰"};
        for (int i = 0; i < labels.length; i++) {
            final int tab = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(2), dp(4), dp(2), dp(3));

            TextView icon = text(icons[i], 19, currentTab == i ? NAVY : MUTED, true);
            icon.setGravity(Gravity.CENTER);
            TextView label = text(labels[i], 10, currentTab == i ? NAVY : MUTED, currentTab == i);
            label.setGravity(Gravity.CENTER);
            item.addView(icon);
            item.addView(label);
            if (currentTab == i) {
                item.setBackground(rounded(0xFFFFF8D8, 13));
            }
            item.setOnClickListener(v -> {
                currentTab = tab;
                render();
            });
            nav.addView(item, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        }
        return nav;
    }

    private void showHome() {
        pageContent.addView(text("BIENVENUE AU CAM93", 12, NAVY, true));
        addSpace(10);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(dp(20), dp(22), dp(20), dp(20));
        hero.setBackground(rounded(NAVY, 22));

        TextView label = text("LE CLUB • L'HISTOIRE • L'AMBITION", 10, YELLOW, true);
        hero.addView(label);
        TextView headline = text("Un club.\nUne passion.\nMontreuil.", 30, WHITE, true);
        headline.setLineSpacing(-dp(1), 1f);
        LinearLayout.LayoutParams headlineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headlineParams.topMargin = dp(12);
        hero.addView(headline, headlineParams);

        TextView description = text("Vice-champion de France des clubs Élite 2026", 15, 0xFFE8EDF5, false);
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descriptionParams.topMargin = dp(12);
        hero.addView(description, descriptionParams);

        TextView button = pillButton("DÉCOUVRIR LE CLUB  →", NAVY, YELLOW);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dp(46));
        buttonParams.topMargin = dp(20);
        hero.addView(button, buttonParams);
        button.setOnClickListener(v -> openWeb(HISTORY, "L'histoire du CAM93"));

        pageContent.addView(hero, matchWrap());
        addSpace(16);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.addView(statCard("1943", "Année de création"), weightedCardParams(1, 6));
        stats.addView(statCard("20 TITRES", "Champions de France depuis 1997"), weightedCardParams(1, 0));
        pageContent.addView(stats);
        addSpace(24);

        addSectionTitle("Accès rapide", "Tout le CAM93 au même endroit");
        ActionItem[] items = {
                new ActionItem("📰", "Actualités", "Les nouvelles du club", NEWS),
                new ActionItem("🏁", "Résultats", "Performances & compétitions", RESULTS),
                new ActionItem("🏃", "Athlètes", "Découvrez les camistes", ATHLETES),
                new ActionItem("🏆", "Records", "Le palmarès du club", RECORDS),
                new ActionItem("✍️", "Inscriptions", "Rejoindre le CAM93", SIGNUP),
                new ActionItem("📍", "Installations", "Stades & lieux d'entraînement", STADIUMS),
                new ActionItem("👟", "Running", "Courir et progresser ensemble", RUNNING),
                new ActionItem("🔐", "Espace adhérents", "Accès au portail Mon Club", MEMBERS)
        };
        addGrid(items);
        addSpace(22);

        addSectionTitle("La vie du club", "Compétitions et rendez-vous");
        addLinkCard("⚡", "Meeting de Montreuil", "Le rendez-vous international", MEETING);
        addLinkCard("🌐", "Site officiel", "Retrouvez toutes les rubriques", HOME);
        addSpace(12);
        addContactCard();
        addFooter();
    }

    private void showNews() {
        addPageTitle("Actualités", "Les infos et les temps forts de la famille CAM93.");
        addLinkCard("📰", "Toutes les actualités", "Consulter les articles et newsletters du club", NEWS);
        addLinkCard("🏁", "Derniers résultats", "Retrouver les performances des athlètes", RESULTS);
        addLinkCard("🏆", "Bilan et records", "Explorer les résultats et le palmarès", RECORDS);
        addLinkCard("📅", "Meeting de Montreuil", "Informations sur le meeting", MEETING);
        addSpace(14);
        addNotice("Pour consulter les dates, articles et résultats les plus récents, ouvrez la rubrique correspondante du site officiel.");
        addFooter();
    }

    private void showClub() {
        addPageTitle("Le club", "Un club historique de l'athlétisme français, fondé en 1943.");
        addLinkCard("📖", "Notre histoire", "De 1943 à aujourd'hui", HISTORY);
        addLinkCard("🏃", "Les athlètes", "Profils et spécialités des camistes", ATHLETES);
        addLinkCard("🏟", "Stades et installations", "Montreuil, Paris 19 et Bobigny", STADIUMS);
        addLinkCard("👟", "Running", "Des entraînements pour progresser", RUNNING);
        addLinkCard("✍️", "S'inscrire au club", "Première inscription ou renouvellement", SIGNUP);
        addLinkCard("🔐", "Espace adhérents", "Planning, démarches et portail Mon Club", MEMBERS);
        addSpace(12);
        addContactCard();
        addFooter();
    }

    private void showMenu() {
        addPageTitle("Explorer", "Accès direct aux rubriques et services du CAM93.");
        addSectionTitle("Compétition", null);
        addLinkCard("🏁", "Résultats", "Résultats et bilans des compétitions", RESULTS);
        addLinkCard("🏆", "Records du club", "Records et performances de référence", RECORDS);
        addLinkCard("🏃", "Athlètes", "Découvrir les athlètes du club", ATHLETES);
        addSpace(8);
        addSectionTitle("Pratique & adhésion", null);
        addLinkCard("✍️", "Inscription en ligne", "Première licence ou renouvellement", SIGNUP);
        addLinkCard("👟", "Running", "Entraînement et progression", RUNNING);
        addLinkCard("📍", "Installations", "Trouver les lieux d'entraînement", STADIUMS);
        addLinkCard("🔐", "Espace adhérents", "Portail Mon Club", MEMBERS);
        addSpace(8);
        addSectionTitle("Le club", null);
        addLinkCard("📖", "Histoire du CAM93", "Depuis 1943", HISTORY);
        addLinkCard("📰", "News", "Actualités et newsletters", NEWS);
        addLinkCard("⚡", "Meeting de Montreuil", "Site de l'événement", MEETING);
        addSpace(12);
        addContactCard();
        addFooter();
    }

    private void addPageTitle(String title, String subtitle) {
        pageContent.addView(text(title, 29, NAVY, true));
        addSpace(6);
        pageContent.addView(text(subtitle, 14, MUTED, false));
        addSpace(18);
    }

    private void addSectionTitle(String title, String subtitle) {
        TextView heading = text(title, 21, TEXT, true);
        pageContent.addView(heading);
        if (subtitle != null) {
            addSpace(3);
            pageContent.addView(text(subtitle, 13, MUTED, false));
        }
        addSpace(12);
    }

    private void addGrid(ActionItem[] items) {
        for (int i = 0; i < items.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            ActionItem first = items[i];
            row.addView(makeGridCard(first), weightedCardParams(1, 6));
            if (i + 1 < items.length) {
                row.addView(makeGridCard(items[i + 1]), weightedCardParams(1, 0));
            } else {
                View spacer = new View(this);
                row.addView(spacer, weightedCardParams(1, 0));
            }
            LinearLayout.LayoutParams rowParams = matchWrap();
            rowParams.bottomMargin = dp(10);
            pageContent.addView(row, rowParams);
        }
    }

    private View makeGridCard(ActionItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(13), dp(15), dp(12), dp(14));
        card.setBackground(rounded(WHITE, 18));
        card.setElevation(dp(1));

        TextView icon = text(item.icon, 24, NAVY, true);
        card.addView(icon);
        TextView title = text(item.title, 15, TEXT, true);
        LinearLayout.LayoutParams titleParams = matchWrap();
        titleParams.topMargin = dp(11);
        card.addView(title, titleParams);
        TextView subtitle = text(item.subtitle, 11, MUTED, false);
        LinearLayout.LayoutParams subtitleParams = matchWrap();
        subtitleParams.topMargin = dp(5);
        card.addView(subtitle, subtitleParams);
        card.setMinimumHeight(dp(122));
        card.setOnClickListener(v -> openWeb(item.url, item.title));
        return card;
    }

    private View statCard(String big, String small) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(13), dp(15), dp(10), dp(14));
        card.setBackground(rounded(WHITE, 16));
        TextView number = text(big, 20, NAVY, true);
        card.addView(number);
        TextView note = text(small, 11, MUTED, false);
        LinearLayout.LayoutParams p = matchWrap();
        p.topMargin = dp(4);
        card.addView(note, p);
        return card;
    }

    private void addLinkCard(String icon, String title, String subtitle, String url) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(13), dp(13), dp(14), dp(13));
        card.setBackground(rounded(WHITE, 17));

        TextView iconBox = text(icon, 22, NAVY, true);
        iconBox.setGravity(Gravity.CENTER);
        iconBox.setBackground(rounded(0xFFFFF4BE, 12));
        card.addView(iconBox, new LinearLayout.LayoutParams(dp(46), dp(46)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        texts.setPadding(dp(12), 0, dp(5), 0);
        texts.addView(text(title, 15, TEXT, true));
        TextView detail = text(subtitle, 12, MUTED, false);
        LinearLayout.LayoutParams detailParams = matchWrap();
        detailParams.topMargin = dp(4);
        texts.addView(detail, detailParams);
        card.addView(texts, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView arrow = text("›", 26, NAVY, true);
        arrow.setGravity(Gravity.CENTER);
        card.addView(arrow, new LinearLayout.LayoutParams(dp(24), dp(42)));
        card.setOnClickListener(v -> openWeb(url, title));

        LinearLayout.LayoutParams params = matchWrap();
        params.bottomMargin = dp(9);
        pageContent.addView(card, params);
    }

    private void addContactCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(17), dp(17), dp(17), dp(17));
        card.setBackground(rounded(NAVY, 19));

        card.addView(text("Une question ?", 20, WHITE, true));
        TextView details = text("CA Montreuil 93\n21 rue des Grands Pêchers\n93100 Montreuil", 13, 0xFFE8EDF5, false);
        LinearLayout.LayoutParams detailsParams = matchWrap();
        detailsParams.topMargin = dp(8);
        card.addView(details, detailsParams);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionsParams = matchWrap();
        actionsParams.topMargin = dp(14);
        card.addView(actions, actionsParams);

        TextView call = pillButton("☎  Appeler", NAVY, YELLOW);
        actions.addView(call, new LinearLayout.LayoutParams(0, dp(43), 1f));
        call.setOnClickListener(v -> openExternal("tel:" + PHONE));
        View gap = new View(this);
        actions.addView(gap, new LinearLayout.LayoutParams(dp(8), 1));
        TextView mail = pillButton("✉  Écrire", WHITE, NAVY);
        actions.addView(mail, new LinearLayout.LayoutParams(0, dp(43), 1f));
        mail.setOnClickListener(v -> openExternal("mailto:" + EMAIL));

        TextView map = text("Ouvrir l'adresse dans Maps  ↗", 12, 0xFFE8EDF5, true);
        map.setPadding(0, dp(15), 0, dp(2));
        card.addView(map);
        map.setOnClickListener(v -> openExternal("https://www.google.com/maps/search/?api=1&query=21+rue+des+Grands+P%C3%AAchers+93100+Montreuil"));
        LinearLayout.LayoutParams params = matchWrap();
        params.topMargin = dp(10);
        pageContent.addView(card, params);
    }

    private void addNotice(String message) {
        TextView notice = text(message, 13, NAVY, false);
        notice.setPadding(dp(15), dp(14), dp(15), dp(14));
        notice.setBackground(rounded(0xFFFFF4BE, 14));
        pageContent.addView(notice, matchWrap());
    }

    private void addFooter() {
        addSpace(18);
        View line = new View(this);
        line.setBackgroundColor(0xFFE0E5EC);
        pageContent.addView(line, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));
        addSpace(12);
        TextView footer = text("CAM93 MOBILE  •  Application non officielle\nContenus et services : cam93.org", 10, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        footer.setLineSpacing(dp(3), 1f);
        pageContent.addView(footer, matchWrap());
        addSpace(8);
    }

    private TextView pillButton(String label, int background, int foreground) {
        TextView button = text(label, 12, foreground, true);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(15), dp(10), dp(15), dp(10));
        button.setBackground(rounded(background, 24));
        return button;
    }

    private TextView text(String value, int sizeSp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setFontFeatureSettings("kern");
        if (bold) tv.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        else tv.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        tv.setIncludeFontPadding(true);
        return tv;
    }

    private GradientDrawable rounded(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private LinearLayout.LayoutParams weightedCardParams(float weight, int rightMarginDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, weight);
        if (rightMarginDp > 0) params.rightMargin = dp(rightMarginDp);
        return params;
    }

    private void addSpace(int heightDp) {
        View space = new View(this);
        pageContent.addView(space, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(heightDp)));
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void openWeb(String url, String title) {
        try {
            Intent intent = new Intent(this, WebContentActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Impossible d'ouvrir cette page.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openExternal(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Aucune application disponible pour cette action.", Toast.LENGTH_SHORT).show();
        }
    }

    private static class ActionItem {
        final String icon;
        final String title;
        final String subtitle;
        final String url;

        ActionItem(String icon, String title, String subtitle, String url) {
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
            this.url = url;
        }
    }
}
