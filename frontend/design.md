# CivicVoice – Design System

This document defines the **visual language, theming rules, and UI principles**
for the CivicVoice web application.

The goal is **consistency without rigidity**.
Components should feel unified, calm, and trustworthy across all pages.

---

## 1. Design Philosophy

### Core Values

- **Trustworthy** – no harsh colors, no visual noise
- **Civic & Neutral** – blue for authority, not decoration
- **Readable** – text contrast always prioritized
- **Soft Structure** – borders over heavy shadows

### Mental Model

> White surfaces on calm backgrounds  
> Blue for action & focus  
> Color always has meaning

---

## 2. Color System (OKLCH)

We use **OKLCH color space** for perceptual consistency,
especially across light and dark modes.

### Why OKLCH?

- Predictable lightness changes
- Same hue across themes
- Better dark mode without guessing

### Token Strategy

**Components never use raw colors.**  
They only use semantic tokens.

---

## 3. Color Tokens

Defined in `globals.css`.

### Brand

| Token             | Purpose                             |
| ----------------- | ----------------------------------- |
| `--primary`       | Primary actions (Post, Submit, CTA) |
| `--primary-hover` | Hover state                         |
| `--primary-soft`  | Background accents & focus rings    |

### Surfaces

| Token             | Purpose          |
| ----------------- | ---------------- |
| `--bg`            | App background   |
| `--surface`       | Cards, panels    |
| `--surface-muted` | Secondary panels |

### Text

| Token          | Purpose         |
| -------------- | --------------- |
| `--text`       | Primary text    |
| `--text-muted` | Secondary text  |
| `--text-soft`  | Meta / disabled |

### Status

| Token       | Purpose            |
| ----------- | ------------------ |
| `--success` | Resolved, positive |
| `--warning` | Pending, caution   |
| `--danger`  | Alerts, errors     |

---

## 4. Light & Dark Mode

Dark mode is applied by toggling the `.dark` class on `<html>`.

```ts
document.documentElement.classList.toggle("dark");
```

## 5. Layout & Surfaces

### Page Wrapper

```ts
<div className="page">
```

Surface (Card / Widget)

```ts
<div className="surface">
```

### Rules

- Always use surfaces for grouped content
- Avoid floating elements without context
- Borders are preferred over strong shadows

## 6. Typography

### Standard Text Roles

| Role       | Usage                   |
| ---------- | ----------------------- |
| Heading    | Page titles             |
| Card Title | Primary card content    |
| Body       | Main readable content   |
| Muted      | Metadata, timestamps    |
| Label      | Section headers, badges |

### Utility Classes

- `.heading`
- `.muted`
- `.subheading`
