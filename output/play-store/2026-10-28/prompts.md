# Imagens da página do Google Play

Geradas com a ferramenta integrada imagegen a partir das artes existentes do projeto. Os arquivos finais foram redimensionados com sips, sem substituir recursos do aplicativo.

- `icone-play-512.png`: 512 × 512 px, PNG.
- `banner-play-1024x500.jpg`: 1024 × 500 px, JPEG sem transparência.

Esses arquivos são artes promocionais. As capturas de tela exigidas pela loja devem mostrar o jogo real em execução.

## Prompt do ícone

Use case: precise-object-edit. Asset type: Google Play store app icon for the existing game MemoryLandGame. Edit target: the attached existing launcher icon. Adapt this exact existing artwork to a full square store icon, requested final canvas exactly 512 by 512 pixels if supported. Preserve the exact yellow card with star, purple card with crescent, their arrangement, white borders, blue sky, green floating island, trees, small castles and waterfall; keep the established illustrated style and colors. Extend the existing landscape and sky naturally into all four corners, removing the pre-rounded icon boundary and black/transparent corners. The artwork must fill the square edge to edge, fully opaque. Clean high resolution edges; no new objects, no text, no badges, no framing or device mockup, no extra drop shadow outside the artwork. This is an adaptation of an existing icon, not a new brand design.

Referência: `app/src/main/res/drawable/ic_launcher_foreground.png`.

## Prompt do banner

Use case: compositing. Asset type: Google Play feature graphic for MemoryLandGame. Edit target: attached existing Memory Land game artwork. Recompose this existing artwork into a polished wide horizontal banner at exactly 1024 by 500 pixels if supported, aspect ratio 2.048:1. Preserve the friendly colorful illustrated style and character identities of the reference. Show the dog and orange cat together on the left, a friendly green dinosaur toward the center, with elephant and musical instruments to the right; use the existing tropical leaves, blue sky and stars as background. Remove vehicles, toys and human dolls to reduce clutter and focus on the actual game themes: animals, dinosaurs and music. Preserve the exact title text 'MEMORY LAND' in two centered lines, yellow MEMORY and white LAND with dark blue outlined backing, crisp readable letters. Position the title centered in the upper-middle safe area, with characters arranged around it without blocking letters. Keep all essential artwork inside a 10 percent safe margin from outer edges. Full-bleed colorful scene, no blank margins, fully opaque. No slogan, no additional text, no app-store logos, no awards, no pricing, no device mockup, no screenshot UI. This is marketing illustration, not a gameplay screenshot.

Referência: `app/src/main/res/drawable/memory_friends_logo_banner.png`.
