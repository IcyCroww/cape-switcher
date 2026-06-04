# Cape Switcher

Cape Switcher is a client-side Fabric mod for Minecraft 1.21.4 that allows you to choose a local custom cape from a folder.

Cape Switcher — клиентский мод для Fabric (Minecraft 1.21.4), который позволяет выбирать локальный кастомный плащ из папки.

## Features / Возможности

- Choose custom cape textures from local PNG files / Выбор кастомных текстур плащей из локальных PNG-файлов
- Three cape modes: Account (vanilla), Local (custom), None (hidden) / Три режима: плащ аккаунта, локальный, без плаща
- In-game GUI wardrobe with 3D player preview / Игровой интерфейс гардероба с 3D-превью игрока
- Drag to rotate model, scroll to zoom / Вращение модели мышью, зум колёсиком
- Hotkey (default: C) to open the cape wardrobe / Хоткей (по умолчанию: C) для открытия гардероба
- Hover preview over cape cards / Предпросмотр при наведении на карточку плаща
- Auto-creates config and capes folders / Автоматическое создание папок
- Error-safe: won't crash on missing/broken files / Не крашит игру при ошибках
- English and Russian localization / Английская и русская локализация

## Installation / Установка

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.4 / Установите Fabric Loader для Minecraft 1.21.4
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) / Установите Fabric API
3. Place `cape-switcher-1.0.0.jar` into your `.minecraft/mods/` folder / Поместите jar в папку `.minecraft/mods/`
4. Launch the game / Запустите игру

## How to Add Capes / Как добавить плащи

1. Navigate to `.minecraft/config/cape-switcher/capes/` / Откройте папку `.minecraft/config/cape-switcher/capes/`
2. Place your PNG cape texture files there (e.g., `my_cape.png`) / Поместите туда PNG-файлы плащей
3. Press **C** in-game to open the Cape Wardrobe / Нажмите **C** в игре для открытия гардероба
4. Click on a cape to select it / Кликните на плащ для выбора

Cape textures should follow the standard Minecraft cape format (64x32 or 128x64 pixels).

Текстуры плащей должны соответствовать стандартному формату Minecraft (64x32 или 128x64 пикселей).

## Cape Modes / Режимы плаща

- **Account Cape / Плащ аккаунта** — Shows your official Minecraft account cape (if you have one) / Показывает официальный плащ аккаунта Minecraft
- **Local Cape / Локальный плащ** — Shows a custom PNG cape from your capes folder / Показывает кастомный PNG-плащ из папки
- **None / Без плаща** — Hides the cape entirely / Полностью скрывает плащ

## Important Notes / Важные замечания

- **This mod is client-side only.** Other players will not see your selected local cape unless they use a compatible sync system.
- **Этот мод работает только на клиенте.** Другие игроки не увидят ваш локальный плащ, если у них нет совместимой системы синхронизации.
- **Cape Switcher does not include or redistribute official Minecraft/Mojang cape textures.** Official account capes are shown only through the normal Minecraft account system using the "Account Cape" mode.
- **Cape Switcher не содержит и не распространяет официальные текстуры плащей Minecraft/Mojang.** Официальные плащи отображаются только через штатную систему аккаунтов Minecraft в режиме "Account Cape".
- To use custom capes, put your own PNG files into `.minecraft/config/cape-switcher/capes/`
- Для использования кастомных плащей поместите свои PNG-файлы в `.minecraft/config/cape-switcher/capes/`

## Building from Source / Сборка из исходников

```
./gradlew build
```

The built jar will be in `build/libs/`.

Собранный jar будет в `build/libs/`.

## License / Лицензия

MIT
