"""Generate the offline app text and a local static page from the same policy."""

from pathlib import Path
import html
import re

ROOT = Path(__file__).resolve().parents[1]
source = ROOT / "docs" / "politica-de-privacidade.md"
destination = ROOT / "app" / "src" / "main" / "assets" / "privacy-policy.txt"
markdown = source.read_text(encoding="utf-8")
policy = re.sub(r"^#{1,6}\s+", "", markdown, flags=re.MULTILINE)
policy = re.sub(r"\[([^\]]+)\]\(([^)]+)\)", r"\1 (\2)", policy)
destination.parent.mkdir(parents=True, exist_ok=True)
destination.write_text(policy, encoding="utf-8")
print(f"Updated {destination.relative_to(ROOT)}")


def inline(text):
    escaped = html.escape(text)
    linked = re.sub(
        r"\[([^\]]+)\]\((https?://[^)]+)\)",
        r'<a href="\2">\1</a>',
        escaped,
    )
    email = "talita.memoryland@gmail.com"
    return linked.replace(email, f'<a href="mailto:{email}">{email}</a>')


blocks = []
contents = []
for block in markdown.strip().split("\n\n"):
    if block.startswith("# "):
        blocks.append(f"<h1>{inline(block[2:])}</h1>")
    elif block.startswith("## "):
        section = f"secao-{len(contents) + 1}"
        title = block[3:]
        label = re.sub(r"^\d+\.\s*", "", title)
        contents.append(f'<li><a href="#{section}">{inline(label)}</a></li>')
        blocks.append(f'<section aria-labelledby="{section}"><h2 id="{section}">{inline(title)}</h2>')
    else:
        text = inline(" ".join(block.splitlines()))
        css = ' class="date"' if block.startswith("Última atualização:") else ""
        blocks.append(f"<p{css}>{text}</p>")

# Close each section before starting the next; the title and update date precede the sections.
body = "\n".join(blocks)
body = body.replace('<section aria-labelledby=', '</section>\n<section aria-labelledby=')
body = body.replace('</section>\n<section', '<section', 1) + "\n</section>"
toc = '<nav aria-label="Sumário"><p>Nesta página</p><ol>' + "".join(contents) + '</ol></nav>'
date_end = body.index("</p>") + len("</p>")
body = body[:date_end] + "\n" + toc + body[date_end:]
site = ROOT / "sites" / "memoryland-privacidade"
template = (site / "template.html").read_text(encoding="utf-8")
page = site / "dist" / "index.html"
page.parent.mkdir(parents=True, exist_ok=True)
page.write_text(template.replace("{{body}}", body), encoding="utf-8")
print(f"Updated {page.relative_to(ROOT)}")
