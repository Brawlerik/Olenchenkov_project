package com.example.olenchenkovproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class FilterState(
    var language: String = "Всі",
    var playerCount: String = "Всі",
    var complexity: String = "Всі",
    var publisher: String = "Всі",
    var genre: String = "Всі",
    var theme: String = "Всі",
    var mechanic: String = "Всі"
)

data class SmartFilterState(
    val playerCount: Int = 0,
    val maxTime: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.favoriteDao()

    val favoriteGamesIds: LiveData<List<FavoriteGameEntity>> = dao.getAllFavorites().asLiveData()
    val filters = MutableLiveData(FilterState())
    val smartFilter = MutableLiveData(SmartFilterState())

    var currentTabMode = "ALL"

    var listStateTab: String = "ALL"
    var listStateCategory: String? = null
    var listStateQuery: String = ""
    var listStateSort: String = "Default"

    val allGenres = listOf("Стратегії", "Карткові", "Варгейми", "Для вечірок", "Вікторини", "Євро-ігри", "Амерітреш", "Логічні", "Тематичні", "Квести", "Мафія", "Рольові", "Пазли", "Сімейні")
    val allThemes = listOf("Фентезі", "Фантастика", "Міфологія", "Цивілізація", "Історичні", "Пригоди", "Дослідження", "Абстрактні", "Економічні", "Будівельні", "Виробництво", "Битви", "На спритність", "Творчі", "На асоціації", "Блеф", "Детектив", "Жахи", "Гумор", "Фільми, книги та відеоігри")
    val allSeries = listOf("Dixit")


    val games = listOf(
        BoardGame(
            1, "Колонізатори (Catan)", "Catan",
            "Класична гра про торгівлю та будівництво. Збирайте ресурси, будуйте дороги та поселення.",
            "3-4 гравців", 3, 4, "60-120 хв", 60, 1200, 1995, "10+",
            "Hobby World", "Мало", "Українська", true,
            listOf("Стратегії", "Сімейні"),
            listOf("Dice Rolling", "Торгівля", "Модульна мапа"),
            listOf("Економічні", "Будівельні"),
            null, 2.3, 7.1,
            "https://upload.wikimedia.org/wikipedia/en/a/a3/Catan-2015-boxart.jpg",
            "https://static0.polygonimages.com/wordpress/wp-content/uploads/2025/04/Board-Full-on-wood-copy.jpg",
            "https://rozetka.com.ua/ua/nastoljnye-igry-i-golovolomki/c98280/vid-22992=kolonizatori/",
            "https://youtu.be/0LR_6dsK6hg?si=D2cMOz7ryQKTn79I"
        ),
        BoardGame(
            2, "Ticket to Ride: Європа", "Ticket to Ride: Europe",
            "Подорожуйте Європою, будуючи залізничні маршрути та виконуючи таємні місії.",
            "2-5 гравців", 2, 5, "30-60 хв", 30, 1400, 2005, "8+",
            "Days of Wonder", "Немає", "Українська", true,
            listOf("Сімейні", "Стратегії"),
            listOf("Збір сетів", "Менеджмент руки", "Будівництво шляхів"),
            listOf("Подорожі", "Будівельні"),
            null, 1.9, 7.5,
            "https://static.yakaboo.ua/media/catalog/product/3/4/342139_42475509.jpg",
            "https://desktopgames.com.ua/games/7761/4.jpg",
            "https://rozetka.com.ua/ua/search/?text=Ticket%20to%20Ride:%20%D0%84%D0%B2%D1%80%D0%BE%D0%BF%D0%B0",
            "https://youtu.be/kGhFlGgvF60?si=gH1kPaXGFjU6kCbQ"
        ),
        BoardGame(
            3, "Коса (Scythe)", "Scythe",
            "Альтернативна історія 1920-х років. Гігантські роботи, економічний двигун та холодний розрахунок.",
            "1-5 гравців", 1, 5, "115 хв", 115, 3500, 2016, "14+", "Kilogames", "Середня", "Українська", true,
            listOf("Стратегії", "Варгейми", "Євро-ігри"),
            listOf("Побудова двигуна", "Контроль території", "Асиметрична стратегія"),
            listOf("Фантастика", "Економічні", "Битви"),
            null, 3.4, 8.2,
            "https://geekach.com.ua/content/images/29/870x716l99br0/kosa-scythe-serp-ukr-43361835141091.webp",
            "https://rozum.com.ua/upload/resize_cache/webp/iblock/360/mx33a00sd1sd2595gu36fu9a94231gzw.webp",
            "https://rozetka.com.ua/ua/279743458/p279743458/",
            "https://youtu.be/-KTCAErOi04?si=AvyWiV4TF3707UQp"
        ),
        BoardGame(
            4, "Діксіт (Dixit)", "Dixit",
            "Гра на асоціації та уяву. Вгадайте карту оповідача серед пасток інших гравців.",
            "3-8 гравців", 3, 8, "30 хв", 30, 950, 2008, "8+", "Igames", "Немає", "Українська", true,
            listOf("Для вечірок", "Карткові"),
            listOf("Голосування", "На асоціації"),
            listOf("Абстрактні", "Творчі"),
            "Dixit", 1.2, 7.2,
            "https://upload.wikimedia.org/wikipedia/uk/7/7b/Dixitgame.jpg",
            "https://royalgames.in.ua/image/cache/catalog/products/dixit/3-1000x1000.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%94%D1%96%D0%BA%D1%81%D1%96%D1%82%20(Dixit)",
            "https://youtu.be/i3Ap7aI59Vo?si=LH9Y6iToSP32UBxn"
        ),
        BoardGame(
            5, "Дюна: Імперіум", "Dune: Imperium",
            "Боротьба за контроль над спайсом на Арракісі. Поєднання декбілдінгу та розміщення робітників.",
            "1-4 гравців", 1, 4, "60-120 хв", 60, 2100, 2020, "13+", "Geekach Games", "Багато", "Українська", true,
            listOf("Стратегії", "Євро-ігри"),
            listOf("Колодобудівна", "Міплплейсмент", "Інтриги"),
            listOf("Фантастика", "Фільми, книги та відеоігри", "Битви"),
            "Dune", 3.0, 8.4,
            "https://geekach.com.ua/content/images/25/1600x1600l99br0/duna-imperium-ukr-dune-imperium-11603351633313.jpg",
            "https://desktopgames.com.ua/games/7923/2s.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%94%D1%8E%D0%BD%D0%B0:%20%D0%86%D0%BC%D0%BF%D0%B5%D1%80%D1%96%D1%83%D0%BC",
            "https://youtu.be/_ZvuyUS3LMw?si=X_NAucqCgoEz9fmR"
        ),
        BoardGame(
            6, "Gloomhaven", "Gloomhaven",
            "Масштабна кооперативна кампанія у фентезійному світі. Тактичні бої без кубиків.",
            "1-4 гравців", 1, 4, "60-120 хв", 60, 4500, 2017, "14+", "Geekach Games", "Дуже багато", "Українська", true,
            listOf("Рольові", "Стратегії", "Квести"),
            listOf("Кооперативна гра", "Тактична гра", "Сценарії"),
            listOf("Фентезі", "Пригоди", "Битви"),
            "Gloomhaven", 3.9, 8.6,
            "https://upload.wikimedia.org/wikipedia/en/e/ee/Gloomhaven_Cover_Art.jpg",
            "https://lordofboards.com.ua/content/images/25/536x337l50nn0/gloomhaven-eng-26368074037167.jpg",
            "https://rozetka.com.ua/ua/search/?text=Gloomhaven",
            "https://youtu.be/z8jKY4bLXKk?si=zwSgXdLiFvrpAmgg"
        ),
        BoardGame(
            7, "Крила (Wingspan)", "Wingspan",
            "Створіть найкращий заповідник для птахів. Красива та мирна стратегія.",
            "1-5 гравців", 1, 5, "40-70 хв", 40, 1800, 2019, "10+", "Igames", "Середня", "Українська", true,
            listOf("Сімейні", "Стратегії", "Карткові"),
            listOf("Побудова двигуна", "Збір сетів", "Драфт"),
            listOf("Природа", "Творчі", "Економічні"),
            null, 2.4, 8.1,
            "https://royalgames.in.ua/image/cache/catalog/products/kryla/krila002-2-1000x1000.png",
            "https://royalgames.in.ua/image/cache/catalog/products/kryla/mghevvz5qr00tnow51tr1vko8z41gam3-1000x1000.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%BD%D0%B0%D1%81%D1%82%D1%96%D0%BB%D1%8C%D0%BD%D0%B0%20%D0%B3%D1%80%D0%B0%20%D0%BA%D1%80%D0%B8%D0%BB%D0%B0",
            "https://youtu.be/z4v3aLsirZI?si=WLn5lnm14rv3waaT"
        ),
        BoardGame(
            8, "Кодові імена", "Codenames",
            "Командна гра на асоціації. Капітани дають підказки, команда шукає своїх агентів.",
            "2-8+ гравців", 2, 10, "15 хв", 15, 700, 2015, "10+", "Feelindigo", "Багато (слів)", "Українська", true,
            listOf("Для вечірок", "Вікторини"),
            listOf("Гра в слова", "Дедукція", "Push Your Luck"),
            listOf("Детектив", "На асоціації"),
            null, 1.3, 7.6,
            "https://rozum.com.ua/upload/resize_cache/webp/iblock/b4f/pygcngepc1ett1tsh4vvezcvjziclcad.webp",
            "https://content1.rozetka.com.ua/goods/images/big_tile/481941885.webp",
            "https://rozetka.com.ua/ua/search/?text=%D0%9A%D0%BE%D0%B4%D0%BE%D0%B2%D1%96%20%D1%96%D0%BC%D0%B5%D0%BD%D0%B0",
            "https://youtu.be/w9lR_V2aLzI?si=-w3EnPKp4YhhnsJA"
        ),
        BoardGame(
            9, "Тераформування Марса", "Terraforming Mars",
            "Корпорації змагаються у перетворенні Марса на придатну для життя планету.",
            "1-5 гравців", 1, 5, "120 хв", 120, 2400, 2016, "12+", "Kilogames", "Багато", "Українська", true,
            listOf("Стратегії", "Євро-ігри"),
            listOf("Побудова двигуна", "Драфт", "Менеджмент ресурсів", "Викладання тайлів"),
            listOf("Фантастика", "Економічні", "Будівельні"),
            null, 3.2, 8.4,
            "https://upload.wikimedia.org/wikipedia/en/f/f0/Terraforming_Mars_board_game_box_cover.jpg",
            "https://cf.geekdo-images.com/P-0-0-0-0-0-0-0-0-0-0-0-0=/fit-in/900x600/filters:no_upscale():strip_icc()/pic3536616.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%A2%D0%B5%D1%80%D0%B0%D1%84%D0%BE%D1%80%D0%BC%D1%83%D0%B2%D0%B0%D0%BD%D0%BD%D1%8F%20%D0%9C%D0%B0%D1%80%D1%81%D0%B0",
            "https://youtu.be/qeEz5Rt5VlU?si=7hKfKCSsUG25X6Gf"
        ),
        BoardGame(
            10, "7 Чудес: Дуель", "7 Wonders Duel",
            "Одна з найкращих ігор суто для двох гравців. Розвивайте цивілізацію та воюйте.",
            "2 гравці", 2, 2, "30 хв", 30, 1100, 2015, "10+", "Igames", "Немає", "Українська", true,
            listOf("Стратегії", "Карткові"),
            listOf("Драфт", "Збір сетів", "Перетягування канату"),
            listOf("Цивілізація", "Історичні", "Економічні"),
            "7 Wonders", 2.2, 8.1,
            "https://cf.geekdo-images.com/zdagMskTF7wJBPjX74XsRw__itemrep/img/x5L93n_pSsxfFZ0Ir-JqtjLf-Jw=/fit-in/246x300/filters:strip_icc()/pic2576399.jpg",
            "https://gavial.com.ua/content/images/1/536x402l50nn0/7-wonders-duel-20484846590519.jpg",
            "https://rozetka.com.ua/ua/search/?text=7%20%D0%A7%D1%83%D0%B4%D0%B5%D1%81:%20%D0%94%D1%83%D0%B5%D0%BB%D1%8C",
            "https://youtu.be/gRIM0UowDy0?si=OpoEVJetmS0c2hgt"
        ),
        BoardGame(
            11, "Екіпаж", "The Crew: Quest for Planet Nine",
            "Кооперативна гра на взятки в космосі. Виконуйте місії в повній тиші.",
            "2-5 гравців", 2, 5, "20 хв", 20, 600, 2019, "10+", "Igames", "Немає", "Українська", true,
            listOf("Карткові", "Логічні"),
            listOf("Кооперативна гра", "Обмежена комунікація"),
            listOf("Фантастика", "Космос"),
            null, 2.0, 7.8,
            "https://desktopgames.com.ua/games/5524/big_box.png",
            "https://geekach.com.ua/content/images/10/536x397l99nn0/ekipazh-poshuki-devyatoi-planeti-the-crew-87205201145767.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%95%D0%BA%D1%96%D0%BF%D0%B0%D0%B6",
            "https://youtu.be/wNn1HjedRqg?si=bpfSujiisiBe0vAq"
        ),
        BoardGame(
            12, "Азул", "Azul",
            "Абстрактна стратегія про укладання красивої плитки. Прості правила, глибокий процес.",
            "2-4 гравців", 2, 4, "30-45 хв", 30, 1300, 2017, "8+", "Plan B Games", "Немає", "Мовонезалежна", false,
            listOf("Сімейні", "Логічні", "Абстрактні"),
            listOf("Драфт", "Викладання тайлів", "Збір сетів"),
            listOf("Абстрактні", "Творчі", "Будівельні"),
            "Azul", 1.8, 7.8,
            "https://woodcat.com.ua/content/images/5/452x536l50nn0/nastilna-hra-azul-azul-86947230749685.png",
            "https://geekach.com.ua/content/images/39/536x351l99nn0/azul-en-16817815734280.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%90%D0%B7%D1%83%D0%BB",
            "https://youtu.be/xPp6nqO5IqM?si=8-lTcSzfqg9hqZCS"
        ),
        BoardGame(
            13, "Root (Корені)", "Root",
            "Варгейм у лісі з милими звірятами. Повна асиметрія: кожен грає у свою власну гру.",
            "2-4 гравців", 2, 4, "60-90 хв", 60, 2200, 2018, "10+", "Geekach Games", "Багато", "Українська", true,
            listOf("Варгейми", "Амерітреш", "Стратегії"),
            listOf("Асиметрична стратегія", "Контроль території", "Dice Rolling"),
            listOf("Фентезі", "Битви", "Пригоди"),
            null, 3.8, 8.1,
            "https://ggwp.com.ua/content/images/24/480x376l50nn0/root-korny-anhlyiskyi-35037240005860.jpg",
            "https://upload.wikimedia.org/wikipedia/commons/9/9f/Turn_1_in_board_game_Root.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%BD%D0%B0%D1%81%D1%82%D1%96%D0%BB%D1%8C%D0%BD%D0%B0%20%D0%B3%D1%80%D0%B0%20Root",
            "https://youtu.be/8sdN5tTS9UM?si=UmrFxv_GI6mDmACT"
        ),
        BoardGame(
            14, "Ark Nova", "Ark Nova",
            "Плануйте та будуйте сучасний зоопарк, підтримуючи природоохоронні проєкти.",
            "1-4 гравців", 1, 4, "90-150 хв", 90, 3200, 2021, "14+", "Igames", "Багато", "Українська", true,
            listOf("Стратегії", "Євро-ігри"),
            listOf("Вибір дій", "Менеджмент руки", "Викладання тайлів"),
            listOf("Економічні", "Природа", "Будівельні"),
            null, 3.7, 8.5,
            "https://shared.akamai.steamstatic.com/store_item_assets/steam/apps/2438990/4372524bdf843af739eba63e55121ea904f7c216/capsule_616x353.jpg?t=1750433107",
            "https://m.media-amazon.com/images/I/A1kpo2JSXLL._AC_UF894,1000_QL80_.jpg",
            "https://rozetka.com.ua/ua/search/?text=Ark%20Nova",
            "https://youtu.be/W0BumsxExGA?si=zSsdWRjBpGWq3ZjP"
        ),
        BoardGame(
            15, "Замки Бургундії", "The Castles of Burgundy",
            "Класичне 'Євро' про розбудову маєтку у середньовічній Франції.",
            "2-4 гравців", 2, 4, "90 хв", 90, 2000, 2011, "12+", "Ravensburger", "Немає", "Мовонезалежна", false,
            listOf("Стратегії", "Євро-ігри"),
            listOf("Dice Rolling", "Викладання тайлів", "Збір сетів"),
            listOf("Історичні", "Економічні", "Будівельні"),
            null, 3.0, 8.1,
            "https://desktopgames.com.ua/games/5897/01.jpg",
            "https://desktopgames.com.ua/games/5897/02.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%97%D0%B0%D0%BC%D0%BA%D0%B8%20%D0%91%D1%83%D1%80%D0%B3%D1%83%D0%BD%D0%B4%D1%96%D1%97",
            "https://youtu.be/-bp1EFMoMrI?si=c8fxpDNCeJKosIjn"
        ),
        BoardGame(
            16, "Brass: Бірмінгем", "Brass: Birmingham",
            "Економічна стратегія про промислову революцію в Англії. Одна з найкращих ігор світу.",
            "2-4 гравців", 2, 4, "60-120 хв", 60, 3300, 2018, "14+", "Lord of Boards", "Мало", "Українська", true,
            listOf("Стратегії", "Економічні"),
            listOf("Побудова мережі", "Менеджмент руки", "Розвиток ринку"),
            listOf("Історичні", "Економічні", "Виробництво"),
            "Brass", 3.9, 8.6,
            "https://cf.geekdo-images.com/x3zxjr-Vw5iU4yDPg70Jgw__itemrep/img/giNUMut4HAl-zWyQkGG0YchmuLI=/fit-in/246x300/filters:strip_icc()/pic3490053.jpg",
            "https://files.rebel.pl/products/100/302/_2015900/rebel-gra-strategiczna-brass-birmingham-foto1.jpg",
            "https://rozetka.com.ua/ua/search/?text=Brass:%20%D0%91%D1%96%D1%80%D0%BC%D1%96%D0%BD%D0%B3%D0%B5%D0%BC",
            "https://youtu.be/wyiqq6OTE70?si=bUqLCAO2cY7z8p4S"
        ),
        BoardGame(
            17, "Пандемія", "Pandemic",
            "Гравці-спеціалісти рятують світ від чотирьох смертельних хвороб.",
            "2-4 гравців", 2, 4, "45 хв", 45, 1200, 2008, "8+", "Igames", "Середня", "Українська", true,
            listOf("Стратегії", "Сімейні"),
            listOf("Кооперативна гра", "Збір сетів", "Точки дій"),
            listOf("Пригоди", "Медицина"),
            null, 2.4, 7.5,
            "https://upload.wikimedia.org/wikipedia/en/3/36/Pandemic_game.jpg",
            "https://geekach.com.ua/content/images/25/536x536l99nn0/pandemiya-pandemic-ukr-97193180171594.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%BD%D0%B0%D1%81%D1%82%D1%96%D0%BB%D1%8C%D0%BD%D0%B0%20%D0%B3%D1%80%D0%B0%20%D0%9F%D0%B0%D0%BD%D0%B4%D0%B5%D0%BC%D1%96%D1%8F",
            "https://youtu.be/YcGOXKd-WgM?si=fsCYosIUxkZxVyQ8"
        ),
        BoardGame(
            18, "Евердел", "Everdell",
            "Будуйте місто для лісових звірят під тінню Вічного Дерева. Неймовірно красива гра.",
            "1-4 гравців", 1, 4, "40-80 хв", 40, 2600, 2018, "13+", "Lord of Boards", "Багато", "Українська", true,
            listOf("Сімейні", "Стратегії"),
            listOf("Міплплейсмент", "Побудова двигуна", "Збір сетів"),
            listOf("Фентезі", "Будівельні", "Тварини"),
            "Everdell", 2.8, 8.0,
            "https://static.lelekan.ua/image/cache/catalog/igri/everdel-ukr/everdel-ukr-igromag-lelekan-cover-800x800.png",
            "https://woodcat.com.ua/content/images/15/536x332l50nn0/nastilna-hra-everdel-everdell-65770569556130.png",
            "https://rozetka.com.ua/ua/search/?text=%D0%95%D0%B2%D0%B5%D1%80%D0%B4%D0%B5%D0%BB",
            "https://youtu.be/H2M-MPwP6wE?si=ldCyrUz5iPEpIWRj"
        ),
        BoardGame(
            19, "Листи Закоханих", "Love Letter",
            "Швидка гра з 16 карт. Передайте любовний лист принцесі та усуньте конкурентів.",
            "2-4 гравців", 2, 4, "20 хв", 20, 400, 2012, "10+", "Lord of Boards", "Середня", "Українська", true,
            listOf("Карткові", "Для вечірок", "Філлер"),
            listOf("Дедукція", "Менеджмент руки", "Take that"),
            listOf("Блеф", "Пригоди"),
            "Love Letter", 1.1, 7.2,
            "https://geekach.com.ua/content/images/35/378x536l99nn0/23540821047802.png",
            "https://b1803394.smushcdn.com/1803394/wp-content/uploads/2022/09/LoveLetter19-Total-Package.png?lossy=1&strip=1&webp=1",
            "https://rozetka.com.ua/ua/search/?text=%D0%9B%D0%B8%D1%81%D1%82%D0%B8%20%D0%97%D0%B0%D0%BA%D0%BE%D1%85%D0%B0%D0%BD%D0%B8%D1%85",
            "https://youtu.be/05LzUHeu97I?si=A_49rpliejmcJAvT"
        ),
        BoardGame(
            20, "Розкіш", "Splendor",
            "Збирайте дорогоцінне каміння та купуйте шахти, щоб вразити дворян.",
            "2-4 гравців", 2, 4, "30 хв", 30, 1100, 2014, "10+", "Space Cowboys", "Немає", "Мовонезалежна", false,
            listOf("Сімейні", "Стратегії", "Абстрактні"),
            listOf("Збір сетів", "Драфт", "Контракти"),
            listOf("Економічні", "Виробництво"),
            "Splendor", 1.7, 7.4,
            "https://igrarium.com.ua/image/cache/catalog/data/baiky/roskosh-ukr/igrarium-roskosh-ukr-1000x1000.jpg",
            "https://woodcat.com.ua/content/images/16/536x536l50nn0/nastilna-hra-rozkish-splendor-55508543978010.png",
            "https://rozetka.com.ua/ua/search/?text=%D0%A0%D0%BE%D0%B7%D0%BA%D1%96%D1%88",
            "https://youtu.be/RONKm-1k7gw?si=EEkdYVhJJ4LsyVLF"
        ),
        BoardGame(
            21, "Діксіт: Одіссея", "Dixit: Odyssey", "Продовження.", "3-12", 3, 12, "30 хв", 30, 1100, 2011, "8+", "Igames", "Немає", "Українська", true,
            listOf("Для вечірок", "Карткові"), listOf("Голосування", "На асоціації"), listOf("Абстрактні", "Творчі"),
            "Dixit", 1.3, 7.4,
            "https://retromagaz.com/uploads/products/ba/31/site_foqmjlbik_90c1e907.png",
            "https://images.prom.ua/4365988773_w640_h640_4365988773.jpg",
            "https://rozetka.com.ua/ua/search/?text=%D0%94%D1%96%D0%BA%D1%81%D1%96%D1%82:%D0%BE%D0%B4%D1%96%D1%81%D0%B5%D1%8F",
            "https://youtu.be/Qi4MoW6NuaQ?si=KLuE2H5mKfi3-RMd"
        )
    )

    fun updateStatus(gameId: Int, newStatus: String) { viewModelScope.launch(Dispatchers.IO) { val current = dao.getGameById(gameId) ?: FavoriteGameEntity(gameId); saveOrDelete(current.copy(status = newStatus)) } }
    fun toggleFavorite(gameId: Int) { viewModelScope.launch(Dispatchers.IO) { val current = dao.getGameById(gameId) ?: FavoriteGameEntity(gameId); saveOrDelete(current.copy(isFavorite = !current.isFavorite)) } }
    fun saveReview(gameId: Int, rating: Int, comment: String, plays: Int) { viewModelScope.launch(Dispatchers.IO) { val current = dao.getGameById(gameId) ?: FavoriteGameEntity(gameId); saveOrDelete(current.copy(userRating = rating, userComment = comment, playCount = plays)) } }
    fun saveNotes(gameId: Int, notes: String) { viewModelScope.launch(Dispatchers.IO) { val current = dao.getGameById(gameId) ?: FavoriteGameEntity(gameId); saveOrDelete(current.copy(gameNotes = notes)) } }
    private suspend fun saveOrDelete(entry: FavoriteGameEntity) { if (entry.status == "NONE" && !entry.isFavorite && entry.userRating == 0 && entry.playCount == 0 && entry.userComment.isEmpty() && entry.gameNotes.isEmpty()) dao.delete(entry) else dao.insert(entry) }
}