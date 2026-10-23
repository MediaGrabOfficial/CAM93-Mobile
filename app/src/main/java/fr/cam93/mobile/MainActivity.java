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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int INK = Color.rgb(7, 15, 28);
    private static final int NAVY = Color.rgb(9, 25, 47);
    private static final int PANEL = Color.rgb(17, 33, 55);
    private static final int PANEL_2 = Color.rgb(22, 42, 66);
    private static final int YELLOW = Color.rgb(255, 211, 31);
    private static final int PAPER = Color.rgb(246, 247, 249);
    private static final int TEXT = Color.rgb(245, 247, 250);
    private static final int MUTED = Color.rgb(164, 178, 198);
    private static final int LINE = Color.rgb(42, 59, 80);

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

    private int currentTab = 0;
    private LinearLayout pageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(INK);
        getWindow().setNavigationBarColor(INK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
        render();
    }

    private void render() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(INK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets safe = insets.getInsets(WindowInsets.Type.systemBars());
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
                return insets;
            });
        }

        root.addView(createHeader(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(68)));

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        pageContent = new LinearLayout(this);
        pageContent.setOrientation(LinearLayout.VERTICAL);
        pageContent.setPadding(dp(16), dp(12), dp(16), dp(22));
        scroll.addView(pageContent);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        root.addView(createBottomNavigation(), new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(64)));

        setContentView(root);

        if (currentTab == 0) showHome();
        else if (currentTab == 1) showNews();
        else if (currentTab == 2) showClub();
        else showMenu();

        animateEntrance(pageContent, 0);
    }

    private View createHeader() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(16), dp(8), dp(16), dp(8));
        bar.setBackgroundColor(INK);

        FrameLayout crest = new FrameLayout(this);
        GradientDrawable crestBg = rounded(YELLOW, 8);
        crest.setBackground(crestBg);
        TextView crestText = text("CAM\n93", 14, INK, true);
        crestText.setGravity(Gravity.CENTER);
        crestText.setLineSpacing(-dp(3), 1f);
        crest.addView(crestText, new FrameLayout.LayoutParams(
                dp(48), dp(48), Gravity.CENTER));
        bar.addView(crest, new LinearLayout.LayoutParams(dp(48), dp(48)));

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(dp(11), 0, 0, 0);
        TextView title = text("CA MONTREUIL 93", 15, TEXT, true);
        title.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        TextView subtitle = text("ATHLÉTISME  ·  DEPUIS 1943", 9, MUTED, true);
        LinearLayout.LayoutParams sub = wrap();
        sub.topMargin = dp(3);
        brand.addView(title);
        brand.addView(subtitle, sub);
        bar.addView(brand, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView site = text("SITE OFFICIEL  ↗", 9, YELLOW, true);
        site.setGravity(Gravity.CENTER);
        site.setPadding(dp(10), dp(10), dp(10), dp(10));
        site.setBackground(rounded(PANEL, 5));
        site.setOnClickListener(v -> openWeb(HOME, "Site officiel"));
        bar.addView(site);
        View accent = new View(this);
        accent.setBackgroundColor(YELLOW);

        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.addView(bar, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(65)));
        wrapper.addView(accent, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(3)));
        return wrapper;
    }

    private View createBottomNavigation() {
        LinearLayout holder = new LinearLayout(this);
        holder.setOrientation(LinearLayout.VERTICAL);
        holder.setBackgroundColor(INK);

        View rule = new View(this);
        rule.setBackgroundColor(LINE);
        holder.addView(rule, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1)));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        String[] names = {"Accueil", "Actualités", "Le club", "Menu"};
        String[] symbols = {"⌂", "≋", "◆", "☰"};
        for (int i = 0; i < names.length; i++) {
            final int tab = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(2), dp(5), dp(2), dp(3));

            TextView icon = text(symbols[i], 19, currentTab == i ? YELLOW : MUTED, true);
            icon.setGravity(Gravity.CENTER);
            TextView label = text(names[i], 10, currentTab == i ? TEXT : MUTED, currentTab == i);
            label.setGravity(Gravity.CENTER);
            item.addView(icon);
            item.addView(label);
            View mark = new View(this);
            mark.setBackgroundColor(currentTab == i ? YELLOW : INK);
            LinearLayout.LayoutParams markP = new LinearLayout.LayoutParams(dp(22), dp(3));
            markP.topMargin = dp(4);
            item.addView(mark, markP);
            item.setOnClickListener(v -> {
                currentTab = tab;
                render();
            });
            nav.addView(item, new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1f));
        }
        holder.addView(nav, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(63)));
        return holder;
    }

    private void showHome() {
        TextView kicker = text("CLUB ATHLÉTIQUE  ·  MONTREUIL", 10, YELLOW, true);
        kicker.setLetterSpacing(0.12f);
        pageContent.addView(kicker);
        addSpace(9);

        FrameLayout hero = new FrameLayout(this);
        hero.setBackground(rounded(NAVY, 17));
        hero.setClipToOutline(true);

        android.widget.ImageView photo = new android.widget.ImageView(this);
        photo.setImageResource(R.drawable.hero_sprint);
        photo.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        photo.setContentDescription("Photo d'illustration d'un sprint sur piste");
        hero.addView(photo, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        View leftShade = new View(this);
        leftShade.setBackground(new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xF207162C, 0xBD07162C, 0x4407162C, 0x1207162C}));
        hero.addView(leftShade, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        View bottomShade = new View(this);
        bottomShade.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x0007162C, 0x2207162C, 0xD907162C}));
        hero.addView(bottomShade, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        TrackHeroView illustration = new TrackHeroView(this);
        hero.addView(illustration, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        LinearLayout copy = new LinearLayout(this);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setGravity(Gravity.BOTTOM);
        copy.setPadding(dp(18), dp(18), dp(18), dp(18));
        TextView since = text("MONTREUIL 93  /  FONDÉ EN 1943", 9, YELLOW, true);
        since.setLetterSpacing(0.11f);
        copy.addView(since);

        TextView headline = text("L’ATHLÉTISME\nSANS LIMITE.", 34, TEXT, true);
        headline.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        headline.setLineSpacing(-dp(4), 1f);
        LinearLayout.LayoutParams titleP = wrap();
        titleP.topMargin = dp(10);
        copy.addView(headline, titleP);

        TextView description = text("Un club historique. Une énergie collective.", 12, 0xFFE0E8F2, false);
        LinearLayout.LayoutParams descP = wrap();
        descP.topMargin = dp(8);
        copy.addView(description, descP);

        TextView cta = text("DÉCOUVRIR LE CLUB   ↗", 11, INK, true);
        cta.setGravity(Gravity.CENTER);
        cta.setPadding(dp(14), dp(12), dp(14), dp(12));
        cta.setBackground(rounded(YELLOW, 5));
        LinearLayout.LayoutParams ctaP = wrap();
        ctaP.topMargin = dp(15);
        copy.addView(cta, ctaP);
        cta.setOnClickListener(v -> openWeb(HISTORY, "Histoire du CAM93"));

        FrameLayout.LayoutParams copyParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.BOTTOM);
        hero.addView(copy, copyParams);
        LinearLayout.LayoutParams heroP = match();
        heroP.height = dp(355);
        pageContent.addView(hero, heroP);
        animateEntrance(hero, 40);

        addStatStrip();
        addSpace(18);

        addSectionTitle("ACCÈS RAPIDE", "Les rubriques essentielles");
        LinearLayout quickRow = new LinearLayout(this);
        quickRow.setOrientation(LinearLayout.HORIZONTAL);
        quickRow.addView(quickAction("01", "Résultats", "Les performances", RESULTS), weighted(1, 6));
        quickRow.addView(quickAction("02", "Inscriptions", "Rejoindre le club", SIGNUP), weighted(1, 0));
        LinearLayout.LayoutParams quickP = match();
        quickP.height = dp(104);
        pageContent.addView(quickRow, quickP);
        addSpace(7);

        addSectionTitle("LA VIE DU CLUB", "Compétitions, athlètes et informations");
        addRow("Actualités", "Les nouvelles du CAM93", NEWS);
        addRow("Résultats", "Compétitions et bilans", RESULTS);
        addRow("Les athlètes", "Les camistes et leurs disciplines", ATHLETES);
        addRow("Records du club", "Les performances de référence", RECORDS);
        addRow("Stades et installations", "Montreuil, Paris 19 et Bobigny", STADIUMS);
        addRow("Running", "Courir et progresser ensemble", RUNNING);
        addRow("Boutique du club", "Les équipements du CAM93", SHOP);
        addRow("Meeting de Montreuil", "Le rendez-vous international", MEETING);
        addRow("Espace adhérents", "Connexion au portail Monclub", MEMBERS);
        addContactSection();
        addFooter();
    }

    private void addStatStrip() {
        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setGravity(Gravity.CENTER_VERTICAL);
        stats.setPadding(dp(6), dp(14), dp(6), dp(14));
        stats.setBackground(rounded(PANEL, 10));

        stats.addView(statCell("20", "TITRES NATIONAUX"), new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        stats.addView(statDivider());
        stats.addView(statCell("2026", "VICE-CHAMPION ÉLITE"), new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        stats.addView(statDivider());
        stats.addView(statCell("1943", "ANNÉE DE CRÉATION"), new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout.LayoutParams params = match();
        params.topMargin = dp(10);
        pageContent.addView(stats, params);
    }

    private View statCell(String number, String label) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(Gravity.CENTER);
        TextView big = text(number, 21, TEXT, true);
        big.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        TextView small = text(label, 8, MUTED, true);
        small.setGravity(Gravity.CENTER);
        small.setLetterSpacing(0.03f);
        LinearLayout.LayoutParams p = wrap();
        p.topMargin = dp(4);
        cell.addView(big);
        cell.addView(small, p);
        return cell;
    }

    private View statDivider() {
        View divider = new View(this);
        divider.setBackgroundColor(LINE);
        divider.setLayoutParams(new LinearLayout.LayoutParams(dp(1), dp(35)));
        return divider;
    }

    private View quickAction(String number, String title, String detail, String url) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER_VERTICAL);
        tile.setPadding(dp(14), dp(12), dp(12), dp(12));
        tile.setBackground(rounded(PANEL_2, 9));

        TextView no = text(number + "  /  CAM93", 9, YELLOW, true);
        no.setLetterSpacing(0.05f);
        tile.addView(no);
        TextView name = text(title, 17, TEXT, true);
        name.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        LinearLayout.LayoutParams nameP = wrap();
        nameP.topMargin = dp(6);
        tile.addView(name, nameP);
        TextView subtitle = text(detail + "  →", 10, MUTED, false);
        LinearLayout.LayoutParams subP = wrap();
        subP.topMargin = dp(3);
        tile.addView(subtitle, subP);
        tile.setOnClickListener(v -> openWeb(url, title));
        return tile;
    }

    private void showNews() {
        addPageTitle("Actualités", "Les dernières infos et les temps forts du CAM93.");
        addFeatureRow("NEWS / 01", "Toute l’actualité du club", "Articles et newsletters", NEWS);
        addRow("Résultats", "Les performances des athlètes", RESULTS);
        addRow("Records", "Le palmarès du club", RECORDS);
        addRow("Meeting de Montreuil", "Informations sur le meeting", MEETING);
        addNotice("Les articles, calendriers et résultats s’ouvrent depuis le site officiel pour rester à jour.");
        addFooter();
    }

    private void showClub() {
        addPageTitle("Le club", "Une histoire montreuilloise commencée en 1943.");
        addFeatureRow("IDENTITÉ / 01", "Un club, une ville, une piste", "Découvrir notre histoire", HISTORY);
        addRow("Les athlètes", "Les camistes et leurs spécialités", ATHLETES);
        addRow("Stades et installations", "Les lieux d’entraînement", STADIUMS);
        addRow("Groupes jeunes", "École d’athlétisme", YOUNG_GROUPS);
        addRow("Groupes de spécialités", "Sprint, fond, sauts, lancers et marche", SPECIALTIES);
        addRow("La boutique", "Équipements du club", SHOP);
        addRow("Meeting de Montreuil", "Le site de l’événement", MEETING);
        addContactSection();
        addFooter();
    }

    private void showMenu() {
        addPageTitle("Explorer", "Tous les services du CA Montreuil 93.");
        addSectionTitle("COMPÉTITION", null);
        addRow("Résultats", "Résultats et classements", RESULTS);
        addRow("Records du club", "Records et performances", RECORDS);
        addRow("Athlètes", "Profils des athlètes", ATHLETES);
        addSectionTitle("ADHÉSION & PRATIQUE", null);
        addRow("Inscription en ligne", "Nouvelle inscription ou renouvellement", SIGNUP);
        addRow("Espace adhérents", "Connexion Monclub", MEMBERS);
        addRow("Espace coachs", "Accès réservé aux coachs", COACHES);
        addRow("Groupes jeunes", "École d’athlétisme", YOUNG_GROUPS);
        addRow("Groupes de spécialités", "Trouver son groupe", SPECIALTIES);
        addRow("Running", "Offres et horaires running", RUNNING);
        addSectionTitle("INFORMATIONS", null);
        addRow("Actualités", "Les nouvelles du club", NEWS);
        addRow("Histoire", "Le CA Montreuil depuis 1943", HISTORY);
        addRow("Installations", "Les stades du CAM93", STADIUMS);
        addRow("Boutique", "Équipements et textiles", SHOP);
        addRow("Meeting de Montreuil", "Site de l’événement", MEETING);
        addRow("Site officiel", "cam93.org", HOME);
        addContactSection();
        addFooter();
    }

    private void addPageTitle(String title, String subtitle) {
        pageContent.addView(text("CAM93  /  ESPACE CLUB", 9, YELLOW, true));
        addSpace(7);
        TextView head = text(title, 31, TEXT, true);
        head.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        pageContent.addView(head);
        addSpace(4);
        pageContent.addView(text(subtitle, 13, MUTED, false));
        addSpace(12);
    }

    private void addSectionTitle(String title, String subtitle) {
        LinearLayout titleBlock = new LinearLayout(this);
        titleBlock.setOrientation(LinearLayout.VERTICAL);

        View dash = new View(this);
        dash.setBackgroundColor(YELLOW);
        LinearLayout.LayoutParams dashP = new LinearLayout.LayoutParams(dp(28), dp(3));
        titleBlock.addView(dash, dashP);

        TextView heading = text(title, 12, TEXT, true);
        heading.setLetterSpacing(0.10f);
        LinearLayout.LayoutParams hP = wrap();
        hP.topMargin = dp(9);
        titleBlock.addView(heading, hP);

        if (subtitle != null) {
            TextView sub = text(subtitle, 11, MUTED, false);
            LinearLayout.LayoutParams sP = wrap();
            sP.topMargin = dp(3);
            titleBlock.addView(sub, sP);
        }
        LinearLayout.LayoutParams params = match();
        params.topMargin = dp(7);
        params.bottomMargin = dp(9);
        pageContent.addView(titleBlock, params);
    }

    private void addFeatureRow(String eyebrow, String title, String subtitle, String url) {
        LinearLayout feature = new LinearLayout(this);
        feature.setOrientation(LinearLayout.VERTICAL);
        feature.setPadding(dp(15), dp(15), dp(15), dp(15));
        feature.setBackground(rounded(PANEL, 10));

        TextView tag = text(eyebrow, 9, YELLOW, true);
        tag.setLetterSpacing(0.08f);
        feature.addView(tag);
        TextView name = text(title, 19, TEXT, true);
        name.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        LinearLayout.LayoutParams nameP = wrap();
        nameP.topMargin = dp(9);
        feature.addView(name, nameP);
        TextView detail = text(subtitle + "  ↗", 11, MUTED, false);
        LinearLayout.LayoutParams detailP = wrap();
        detailP.topMargin = dp(4);
        feature.addView(detail, detailP);
        feature.setOnClickListener(v -> openWeb(url, title));

        LinearLayout.LayoutParams params = match();
        params.bottomMargin = dp(8);
        pageContent.addView(feature, params);
        animateEntrance(feature, 50);
    }

    private void addRow(String title, String subtitle, String url) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12), dp(12), dp(10), dp(12));
        row.setBackground(rounded(PANEL, 7));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        TextView name = text(title, 14, TEXT, true);
        name.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        titles.addView(name);
        if (subtitle != null && !subtitle.isEmpty()) {
            TextView sub = text(subtitle, 11, MUTED, false);
            LinearLayout.LayoutParams subP = wrap();
            subP.topMargin = dp(3);
            titles.addView(sub, subP);
        }
        row.addView(titles, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView arrow = text("↗", 17, YELLOW, true);
        arrow.setGravity(Gravity.CENTER);
        row.addView(arrow, new LinearLayout.LayoutParams(dp(28), dp(34)));
        row.setOnClickListener(v -> openWeb(url, title));

        LinearLayout.LayoutParams params = match();
        params.bottomMargin = dp(6);
        pageContent.addView(row, params);
    }

    private void addContactSection() {
        addSpace(10);
        addSectionTitle("NOUS CONTACTER", "Le club à Montreuil");
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(14), dp(14), dp(14), dp(14));
        panel.setBackground(rounded(PANEL_2, 9));
        panel.addView(text("CA Montreuil 93", 17, TEXT, true));
        TextView address = text("21 rue des Grands Pêchers\n93100 Montreuil", 12, MUTED, false);
        LinearLayout.LayoutParams addressP = wrap();
        addressP.topMargin = dp(6);
        panel.addView(address, addressP);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams actionsP = match();
        actionsP.topMargin = dp(13);
        panel.addView(actions, actionsP);

        TextView call = text("APPELER  ↗", 10, INK, true);
        call.setGravity(Gravity.CENTER);
        call.setBackground(rounded(YELLOW, 5));
        actions.addView(call, new LinearLayout.LayoutParams(0, dp(42), 1f));
        call.setOnClickListener(v -> openExternal("tel:" + PHONE));

        View gap = new View(this);
        actions.addView(gap, new LinearLayout.LayoutParams(dp(8), 1));

        TextView mail = text("ÉCRIRE  ↗", 10, TEXT, true);
        mail.setGravity(Gravity.CENTER);
        mail.setBackground(rounded(INK, 5));
        actions.addView(mail, new LinearLayout.LayoutParams(0, dp(42), 1f));
        mail.setOnClickListener(v -> openExternal("mailto:" + EMAIL));

        TextView map = text("OUVRIR DANS MAPS  ↗", 10, TEXT, true);
        map.setGravity(Gravity.CENTER);
        map.setPadding(dp(12), dp(9), dp(12), dp(9));
        LinearLayout.LayoutParams mapP = match();
        mapP.topMargin = dp(7);
        panel.addView(map, mapP);
        map.setOnClickListener(v -> openExternal(MAPS));

        pageContent.addView(panel, match());
    }

    private void addNotice(String message) {
        TextView note = text(message, 12, TEXT, false);
        note.setPadding(dp(13), dp(13), dp(13), dp(13));
        note.setBackground(rounded(PANEL, 7));
        LinearLayout.LayoutParams params = match();
        params.topMargin = dp(14);
        pageContent.addView(note, params);
    }

    private void addFooter() {
        addSpace(18);
        View rule = new View(this);
        rule.setBackgroundColor(LINE);
        LinearLayout.LayoutParams ruleP = match();
        ruleP.height = dp(1);
        pageContent.addView(rule, ruleP);
        addSpace(10);
        TextView footer = text("CAM93 MOBILE  ·  APPLICATION NON OFFICIELLE\nCONTENUS & SERVICES : CAM93.ORG", 9, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        footer.setLineSpacing(dp(3), 1f);
        pageContent.addView(footer, match());
    }

    private void animateEntrance(View view, long delay) {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator(1.5f));
        AlphaAnimation fade = new AlphaAnimation(0f, 1f);
        fade.setDuration(320);
        TranslateAnimation rise = new TranslateAnimation(0, 0, dp(12), 0);
        rise.setDuration(380);
        set.addAnimation(fade);
        set.addAnimation(rise);
        set.setStartOffset(delay);
        view.startAnimation(set);
    }

    private TextView text(String value, int sizeSp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER_VERTICAL);
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

    private LinearLayout.LayoutParams weighted(float weight, int rightMarginDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, weight);
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
            Uri uri = Uri.parse(url);
            String scheme = uri.getScheme();
            if ("tel".equalsIgnoreCase(scheme) || "mailto".equalsIgnoreCase(scheme)) {
                openExternal(url);
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

    private void openExternal(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Aucune application disponible pour ce lien.", Toast.LENGTH_SHORT).show();
        }
    }
}
