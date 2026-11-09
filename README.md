# The Forgotten World — 0.1 "First Awakening"

Лёгкий Fabric-мод для Minecraft Java **1.21.11**. Без клиентского кода, без кастомных рендеров и сторонних библиотек (только Fabric API) — подходит для Android (Zalith Launcher + MobileGlues).

A lightweight Fabric mod for Minecraft Java **1.21.11**. No client code, no custom renderers, no extra libraries (Fabric API only) — suitable for Android (Zalith Launcher + MobileGlues).

## Содержимое / Content

| Предмет / Item | ID | Описание |
|---|---|---|
| Эфирный осколок / Ether Shard | `forgottenworld:ether_shard` | Ресурс, выпадает из эфирной руды. |
| Эфирная руда / Ether Ore | `forgottenworld:ether_ore` | Генерируется в Верхнем мире (Y от -48 до 32), нужна железная кирка. Дроп: 1–3 осколка (Удача увеличивает), Шёлковое касание даёт саму руду. Даёт опыт, слабо светится. |
| Посох Пустоты / Void Staff | `forgottenworld:void_staff` | ПКМ — рывок вперёд по линии взгляда до 12 блоков. Останавливается перед стеной, не помещает в блоки, гасит урон от падения, перезарядка 3 сек, 200 прочности. |

### Рецепты / Recipes

Посох Пустоты (верстак), `S` — палка, `#` — эфирный осколок, `E` — жемчуг Эндера:

```
 # E
 S #
 S
```

Эфирный осколок также получается переплавкой эфирной руды (печь / плавильная печь).

## Установка (игроку)

1. Установите Fabric Loader (0.18.1 или новее) для Minecraft 1.21.11.
2. Скачайте Fabric API **0.141.1+1.21.11** (Modrinth/CurseForge).
3. Положите `fabric-api-*.jar` и `the-forgotten-world-0.1.0.jar` в папку `mods`.

### Android (Zalith Launcher)

1. В Zalith Launcher создайте версию 1.21.11 и установите Fabric через встроенный установщик загрузчиков.
2. Рендерер: MobileGlues (мод его не требует и ничего не рисует сам).
3. Скопируйте оба `.jar` в папку `mods` игрового профиля (кнопка папки/менеджер файлов лаунчера).
4. Java 21 (выберите в настройках профиля).

## Сборка из исходников

Требования: **JDK 21**, **Gradle 9.2+** (для Fabric Loom 1.14), доступ в интернет при первой сборке.

```bash
# один раз: сгенерировать gradle wrapper (jar-файл wrapper не входит в архив)
gradle wrapper --gradle-version 9.2.0

# сборка
./gradlew build          # Windows: gradlew.bat build
```

Готовый мод: `build/libs/the-forgotten-world-0.1.0.jar` (не берите `-sources.jar`).

Запуск тестового клиента: `./gradlew runClient`.

## Сборка в облаке через GitHub Actions (без Gradle на телефоне)

1. Создайте пустой репозиторий на GitHub и загрузите туда содержимое этой папки.
2. Откройте вкладку **Actions** и запустите workflow **Build The Forgotten World** через **Run workflow** (или дождитесь автоматического запуска после push).
3. Дождитесь завершения сборки.
4. Внизу страницы запуска откройте **Artifacts** и скачайте `the-forgotten-world-1.21.11`.
5. Внутри архива должен быть `the-forgotten-world-0.1.0.jar`. Установите его вместе с Fabric API в папку `mods`.

Важно: этот workflow автоматизирует сборку, но не означает, что JAR уже собран. Готовый JAR появится только после успешного запуска GitHub Actions.

## Версии / Versions

- Minecraft 1.21.11, маппинги Mojang (official)
- Fabric Loader 0.18.1, Fabric API 0.141.1+1.21.11
- Fabric Loom 1.14, Java 21
- Версии меняются в `gradle.properties`.

## Структура / Layout

- `src/main/java/com/forgottenworld/` — код (`ForgottenWorld`, `block/ModBlocks`, `item/ModItems`, `item/VoidStaffItem`, `worldgen/ModWorldGen`)
- `src/main/resources/assets/forgottenworld/` — модели, blockstate, текстуры (16×16 PNG), языки `en_us` и `ru_ru`
- `src/main/resources/data/forgottenworld/` — лут, рецепты, генерация руды

## Лицензия

MIT
