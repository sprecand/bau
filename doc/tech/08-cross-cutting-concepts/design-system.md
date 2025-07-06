# Bau Platform Design System

## Overview


The Bau Platform uses a modern, comprehensive design system built on OKLCH color space, providing superior accessibility, dark mode support, and perceptual uniformity. This system integrates seamlessly with Angular Material and Tailwind CSS.

## Color System


### OKLCH Advantages


- **Perceptual Uniformity**: Consistent lightness across all hues
- **Better Accessibility**: Improved contrast ratios and readability
- **Future-Proof**: Modern CSS standard with excellent browser support
- **Predictable**: Mathematical color relationships

### Color Tokens


#### Core Colors


```css
:root {
  /*Backgrounds*/
--background: oklch(1 0 0);                    /*Pure white*/
--foreground: oklch(0.1450 0 0);              /*Near black text*/

  /*Cards & Surfaces*/
--card: oklch(1 0 0);                         /*White cards*/
--card-foreground: oklch(0.1450 0 0);         /*Dark text on cards*/
--popover: oklch(1 0 0);                      /*White popovers*/
--popover-foreground: oklch(0.1450 0 0);      /*Dark text in popovers*/
}

```bash

:root {
  /*Backgrounds*/
--background: oklch(1 0 0);                    /*Pure white*/
--foreground: oklch(0.1450 0 0);              /*Near black text*/

  /*Cards & Surfaces*/
--card: oklch(1 0 0);                         /*White cards*/
--card-foreground: oklch(0.1450 0 0);         /*Dark text on cards*/
--popover: oklch(1 0 0);                      /*White popovers*/
--popover-foreground: oklch(0.1450 0 0);      /*Dark text in popovers*/
}

```bash

#### Brand Colors


```css

#### Brand Colors


```css

:root {
  /*Primary Brand*/
--primary: oklch(0.6324 0.1363 157.8607);     /*Professional green*/
--primary-foreground: oklch(0.9850 0 0);      /*White text on primary*/

  /*Secondary*/
--secondary: oklch(0.9700 0 0);               /*Light gray*/
--secondary-foreground: oklch(0.2050 0 0);    /*Dark text on secondary*/

  /*Accent*/
--accent: oklch(0.9700 0 0);                  /*Light gray accent*/
--accent-foreground: oklch(0.2050 0 0);       /*Dark text on accent*/
}

```bash

:root {
  /*Primary Brand*/
--primary: oklch(0.6324 0.1363 157.8607);     /*Professional green*/
--primary-foreground: oklch(0.9850 0 0);      /*White text on primary*/

  /*Secondary*/
--secondary: oklch(0.9700 0 0);               /*Light gray*/
--secondary-foreground: oklch(0.2050 0 0);    /*Dark text on secondary*/

  /*Accent*/
--accent: oklch(0.9700 0 0);                  /*Light gray accent*/
--accent-foreground: oklch(0.2050 0 0);       /*Dark text on accent*/
}

```bash

#### State Colors


```css

#### State Colors


```css

:root {
  /*Destructive Actions*/
--destructive: oklch(0.5770 0.2450 27.3250);     /*Red for delete/error*/
--destructive-foreground: oklch(1 0 0);           /*White text on red*/

  /*Muted Elements*/
--muted: oklch(0.9700 0 0);                       /*Subtle background*/
--muted-foreground: oklch(0.5560 0 0);            /*Muted text*/
}

```bash

:root {
  /*Destructive Actions*/
--destructive: oklch(0.5770 0.2450 27.3250);     /*Red for delete/error*/
--destructive-foreground: oklch(1 0 0);           /*White text on red*/

  /*Muted Elements*/
--muted: oklch(0.9700 0 0);                       /*Subtle background*/
--muted-foreground: oklch(0.5560 0 0);            /*Muted text*/
}

```bash

#### UI Elements


```css

#### UI Elements


```css

:root {
  /*Borders & Inputs*/
--border: oklch(0.9220 0 0);                      /*Subtle borders*/
--input: oklch(0.9220 0 0);                       /*Input backgrounds*/
--ring: oklch(0.7080 0 0);                        /*Focus rings*/
}

```bash

:root {
  /*Borders & Inputs*/
--border: oklch(0.9220 0 0);                      /*Subtle borders*/
--input: oklch(0.9220 0 0);                       /*Input backgrounds*/
--ring: oklch(0.7080 0 0);                        /*Focus rings*/
}

```bash

#### Sidebar


```css

#### Sidebar


```css

:root {
  /*Sidebar Navigation*/
--sidebar: oklch(0.9850 0 0);                     /*Sidebar background*/
--sidebar-foreground: oklch(0.1450 0 0);          /*Sidebar text*/
--sidebar-primary: oklch(0.2050 0 0);             /*Active nav items*/
--sidebar-primary-foreground: oklch(0.9850 0 0);  /*Text on active items*/
--sidebar-accent: oklch(0.9700 0 0);              /*Hover states*/
--sidebar-accent-foreground: oklch(0.2050 0 0);   /*Text on hover*/
--sidebar-border: oklch(0.9220 0 0);              /*Sidebar borders*/
--sidebar-ring: oklch(0.7080 0 0);                /*Focus in sidebar*/
}

```bash

:root {
  /*Sidebar Navigation*/
--sidebar: oklch(0.9850 0 0);                     /*Sidebar background*/
--sidebar-foreground: oklch(0.1450 0 0);          /*Sidebar text*/
--sidebar-primary: oklch(0.2050 0 0);             /*Active nav items*/
--sidebar-primary-foreground: oklch(0.9850 0 0);  /*Text on active items*/
--sidebar-accent: oklch(0.9700 0 0);              /*Hover states*/
--sidebar-accent-foreground: oklch(0.2050 0 0);   /*Text on hover*/
--sidebar-border: oklch(0.9220 0 0);              /*Sidebar borders*/
--sidebar-ring: oklch(0.7080 0 0);                /*Focus in sidebar*/
}

```bash

### Dark Mode


```css

### Dark Mode


```css

.dark {
--background: oklch(0.1450 0 0);                  /*Dark background*/
--foreground: oklch(0.9850 0 0);                  /*Light text*/
--card: oklch(0.2050 0 0);                        /*Dark cards*/
--card-foreground: oklch(0.9850 0 0);             /*Light text on cards*/
--popover: oklch(0.2690 0 0);                     /*Dark popovers*/
--popover-foreground: oklch(0.9850 0 0);          /*Light text in popovers*/

--primary: oklch(0.6324 0.1363 157.8607);         /*Same green (works in dark)*/
--primary-foreground: oklch(0.2535 0.0341 296.6556); /*Dark text on green*/

--secondary: oklch(0.2535 0.0341 296.6556);       /*Dark secondary*/
--secondary-foreground: oklch(1.0000 0 0);        /*White text*/

--muted: oklch(0.2690 0 0);                       /*Dark muted*/
--muted-foreground: oklch(0.7080 0 0);            /*Light muted text*/

--accent: oklch(0.3710 0 0);                      /*Dark accent*/
--accent-foreground: oklch(0.9850 0 0);           /*Light text on accent*/

--destructive: oklch(0.7040 0.1910 22.2160);      /*Lighter red for dark mode*/
--destructive-foreground: oklch(0.9850 0 0);      /*Light text on red*/

--border: oklch(0.2750 0 0);                      /*Dark borders*/
--input: oklch(0.3250 0 0);                       /*Dark input backgrounds*/
--ring: oklch(0.5560 0 0);                        /*Dark focus rings*/

  /*Dark Sidebar*/
--sidebar: oklch(0.2050 0 0);                     /*Dark sidebar*/
--sidebar-foreground: oklch(0.9850 0 0);          /*Light sidebar text*/
--sidebar-primary: oklch(0.4880 0.2430 264.3760); /*Purple active items*/
--sidebar-primary-foreground: oklch(0.9850 0 0);  /*Light text on active*/
--sidebar-accent: oklch(0.2690 0 0);              /*Dark hover states*/
--sidebar-accent-foreground: oklch(0.9850 0 0);   /*Light text on hover*/
--sidebar-border: oklch(0.2750 0 0);              /*Dark sidebar borders*/
--sidebar-ring: oklch(0.4390 0 0);                /*Dark focus in sidebar*/
}

```bash

.dark {
--background: oklch(0.1450 0 0);                  /*Dark background*/
--foreground: oklch(0.9850 0 0);                  /*Light text*/
--card: oklch(0.2050 0 0);                        /*Dark cards*/
--card-foreground: oklch(0.9850 0 0);             /*Light text on cards*/
--popover: oklch(0.2690 0 0);                     /*Dark popovers*/
--popover-foreground: oklch(0.9850 0 0);          /*Light text in popovers*/

--primary: oklch(0.6324 0.1363 157.8607);         /*Same green (works in dark)*/
--primary-foreground: oklch(0.2535 0.0341 296.6556); /*Dark text on green*/

--secondary: oklch(0.2535 0.0341 296.6556);       /*Dark secondary*/
--secondary-foreground: oklch(1.0000 0 0);        /*White text*/

--muted: oklch(0.2690 0 0);                       /*Dark muted*/
--muted-foreground: oklch(0.7080 0 0);            /*Light muted text*/

--accent: oklch(0.3710 0 0);                      /*Dark accent*/
--accent-foreground: oklch(0.9850 0 0);           /*Light text on accent*/

--destructive: oklch(0.7040 0.1910 22.2160);      /*Lighter red for dark mode*/
--destructive-foreground: oklch(0.9850 0 0);      /*Light text on red*/

--border: oklch(0.2750 0 0);                      /*Dark borders*/
--input: oklch(0.3250 0 0);                       /*Dark input backgrounds*/
--ring: oklch(0.5560 0 0);                        /*Dark focus rings*/

  /*Dark Sidebar*/
--sidebar: oklch(0.2050 0 0);                     /*Dark sidebar*/
--sidebar-foreground: oklch(0.9850 0 0);          /*Light sidebar text*/
--sidebar-primary: oklch(0.4880 0.2430 264.3760); /*Purple active items*/
--sidebar-primary-foreground: oklch(0.9850 0 0);  /*Light text on active*/
--sidebar-accent: oklch(0.2690 0 0);              /*Dark hover states*/
--sidebar-accent-foreground: oklch(0.9850 0 0);   /*Light text on hover*/
--sidebar-border: oklch(0.2750 0 0);              /*Dark sidebar borders*/
--sidebar-ring: oklch(0.4390 0 0);                /*Dark focus in sidebar*/
}

```bash

## Typography


### Font Stack


```css

## Typography


### Font Stack


```css

:root {
--font-sans: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont,

```bash

'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans',
sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji',
'Segoe UI Symbol', 'Noto Color Emoji';

```bash

--font-serif: ui-serif, Georgia, Cambria, "Times New Roman", Times, serif;
--font-mono: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas,

```bash

"Liberation Mono", "Courier New", monospace;

```bash

}

```bash

:root {
--font-sans: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont,

```bash

'Segoe UI', Roboto, 'Helvetica Neue', Arial, 'Noto Sans',
sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji',
'Segoe UI Symbol', 'Noto Color Emoji';

```bash

--font-serif: ui-serif, Georgia, Cambria, "Times New Roman", Times, serif;
--font-mono: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas,

```bash

"Liberation Mono", "Courier New", monospace;

```bash

}

```bash

### Typography Scale


```css

### Typography Scale


```css

/*Headings*/
.text-4xl { font-size: 2.25rem; line-height: 2.5rem; }    /*36px*/
.text-3xl { font-size: 1.875rem; line-height: 2.25rem; }  /*30px*/
.text-2xl { font-size: 1.5rem; line-height: 2rem; }       /*24px*/
.text-xl { font-size: 1.25rem; line-height: 1.75rem; }    /*20px*/
.text-lg { font-size: 1.125rem; line-height: 1.75rem; }   /*18px*/

/*Body*/
.text-base { font-size: 1rem; line-height: 1.5rem; }      /*16px*/
.text-sm { font-size: 0.875rem; line-height: 1.25rem; }   /*14px*/
.text-xs { font-size: 0.75rem; line-height: 1rem; }       /*12px*/

```bash

/*Headings*/
.text-4xl { font-size: 2.25rem; line-height: 2.5rem; }    /*36px*/
.text-3xl { font-size: 1.875rem; line-height: 2.25rem; }  /*30px*/
.text-2xl { font-size: 1.5rem; line-height: 2rem; }       /*24px*/
.text-xl { font-size: 1.25rem; line-height: 1.75rem; }    /*20px*/
.text-lg { font-size: 1.125rem; line-height: 1.75rem; }   /*18px*/

/*Body*/
.text-base { font-size: 1rem; line-height: 1.5rem; }      /*16px*/
.text-sm { font-size: 0.875rem; line-height: 1.25rem; }   /*14px*/
.text-xs { font-size: 0.75rem; line-height: 1rem; }       /*12px*/

```bash

## Spacing & Layout


### Border Radius


```css

## Spacing & Layout


### Border Radius


```css

:root {
--radius: 0.625rem;  /*10px base radius*/
}

/*Tailwind classes*/
.rounded-sm { border-radius: calc(var(--radius) - 4px); }  /*6px*/
.rounded-md { border-radius: calc(var(--radius) - 2px); }  /*8px*/
.rounded-lg { border-radius: var(--radius); }              /*10px*/
.rounded-xl { border-radius: calc(var(--radius) + 4px); }  /*14px*/

```bash

:root {
--radius: 0.625rem;  /*10px base radius*/
}

/*Tailwind classes*/
.rounded-sm { border-radius: calc(var(--radius) - 4px); }  /*6px*/
.rounded-md { border-radius: calc(var(--radius) - 2px); }  /*8px*/
.rounded-lg { border-radius: var(--radius); }              /*10px*/
.rounded-xl { border-radius: calc(var(--radius) + 4px); }  /*14px*/

```bash

### Shadows


```css

### Shadows


```css

:root {
--shadow-sm: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 1px 2px -1px hsl(0 0% 0% / 0.10);
--shadow-md: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 2px 4px -1px hsl(0 0% 0% / 0.10);
--shadow-lg: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 4px 6px -1px hsl(0 0% 0% / 0.10);
--shadow-xl: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 8px 10px -1px hsl(0 0% 0% / 0.10);
}

```bash

:root {
--shadow-sm: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 1px 2px -1px hsl(0 0% 0% / 0.10);
--shadow-md: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 2px 4px -1px hsl(0 0% 0% / 0.10);
--shadow-lg: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 4px 6px -1px hsl(0 0% 0% / 0.10);
--shadow-xl: 0 1px 3px 0px hsl(0 0% 0% / 0.10), 0 8px 10px -1px hsl(0 0% 0% / 0.10);
}

```bash

## Usage Guidelines


### Color Usage


#### Primary Color


- **Use for**: Main CTAs, active states, brand elements
- **Don't use for**: Large backgrounds, body text
- **Example**: Submit buttons, active navigation items

#### Secondary Color


- **Use for**: Supporting actions, inactive states
- **Don't use for**: Primary actions, important alerts
- **Example**: Cancel buttons, form labels

#### Destructive Color


- **Use for**: Delete actions, error states, warnings
- **Don't use for**: Success messages, neutral actions
- **Example**: Delete buttons, error messages

#### Muted Colors


- **Use for**: Placeholder text, disabled states, subtle backgrounds
- **Don't use for**: Important information, primary actions
- **Example**: Form placeholders, disabled buttons

### Component Patterns


#### Cards


```html

## Usage Guidelines


### Color Usage


#### Primary Color


- **Use for**: Main CTAs, active states, brand elements
- **Don't use for**: Large backgrounds, body text
- **Example**: Submit buttons, active navigation items

#### Secondary Color


- **Use for**: Supporting actions, inactive states
- **Don't use for**: Primary actions, important alerts
- **Example**: Cancel buttons, form labels

#### Destructive Color


- **Use for**: Delete actions, error states, warnings
- **Don't use for**: Success messages, neutral actions
- **Example**: Delete buttons, error messages

#### Muted Colors


- **Use for**: Placeholder text, disabled states, subtle backgrounds
- **Don't use for**: Important information, primary actions
- **Example**: Form placeholders, disabled buttons

### Component Patterns


#### Cards


```html
<mat-card class="bg-card border border-border shadow-md rounded-lg">
  <mat-card-header class="border-b border-border pb-4">

```bash

<mat-card-title class="text-card-foreground text-xl font-semibold">
  Card Title
</mat-card-title>
<mat-card-subtitle class="text-muted-foreground">
  Card subtitle
</mat-card-subtitle>

```bash

  </mat-card-header>

  <mat-card-content class="pt-4">

```bash

<p class="text-card-foreground">Card content goes here.</p>

```bash

  </mat-card-content>

  <mat-card-actions class="border-t border-border pt-4">

```bash

<button mat-raised-button class="bg-primary text-primary-foreground">
  Primary Action
</button>
<button mat-button class="text-muted-foreground">
  Secondary Action
</button>

```bash

  </mat-card-actions>
</mat-card>

```bash

<mat-card class="bg-card border border-border shadow-md rounded-lg">
  <mat-card-header class="border-b border-border pb-4">

```bash

<mat-card-title class="text-card-foreground text-xl font-semibold">
  Card Title
</mat-card-title>
<mat-card-subtitle class="text-muted-foreground">
  Card subtitle
</mat-card-subtitle>

```bash

  </mat-card-header>

  <mat-card-content class="pt-4">

```bash

<p class="text-card-foreground">Card content goes here.</p>

```bash

  </mat-card-content>

  <mat-card-actions class="border-t border-border pt-4">

```bash

<button mat-raised-button class="bg-primary text-primary-foreground">
  Primary Action
</button>
<button mat-button class="text-muted-foreground">
  Secondary Action
</button>

```bash

  </mat-card-actions>
</mat-card>

```bash

#### Forms


```html

#### Forms


```html

<form class="space-y-6 bg-card p-6 rounded-lg border border-border">
  <div class="space-y-4">

```bash

<mat-form-field class="w-full">
  <mat-label class="text-muted-foreground">Company Name</mat-label>
  <input matInput
         class="bg-input border-border text-foreground"
         placeholder="Enter company name">
</mat-form-field>

```bash

```bash

<mat-form-field class="w-full">
  <mat-label class="text-muted-foreground">Email</mat-label>
  <input matInput
         type="email"
         class="bg-input border-border text-foreground"
         placeholder="Enter email address">
</mat-form-field>

```bash

  </div>

  <div class="flex justify-end space-x-3">

```bash

<button type="button"
        mat-button
        class="text-muted-foreground hover:text-foreground">
  Cancel
</button>
<button type="submit"
        mat-raised-button
        class="bg-primary text-primary-foreground hover:bg-primary/90">
  Save Company
</button>

```bash

  </div>
</form>

```bash

<form class="space-y-6 bg-card p-6 rounded-lg border border-border">
  <div class="space-y-4">

```bash

<mat-form-field class="w-full">
  <mat-label class="text-muted-foreground">Company Name</mat-label>
  <input matInput
         class="bg-input border-border text-foreground"
         placeholder="Enter company name">
</mat-form-field>

```bash

```bash

<mat-form-field class="w-full">
  <mat-label class="text-muted-foreground">Email</mat-label>
  <input matInput
         type="email"
         class="bg-input border-border text-foreground"
         placeholder="Enter email address">
</mat-form-field>

```bash

  </div>

  <div class="flex justify-end space-x-3">

```bash

<button type="button"
        mat-button
        class="text-muted-foreground hover:text-foreground">
  Cancel
</button>
<button type="submit"
        mat-raised-button
        class="bg-primary text-primary-foreground hover:bg-primary/90">
  Save Company
</button>

```bash

  </div>
</form>

```bash

#### Navigation


```html

#### Navigation


```html

<nav class="bg-sidebar border-r border-sidebar-border h-full">
  <div class="p-4">

```bash

<h2 class="text-sidebar-foreground text-lg font-semibold mb-4">
  Bau Platform
</h2>

```bash

```bash

<ul class="space-y-2">
  <li>
    <a href="/dashboard"
       class="flex items-center px-3 py-2 rounded-md bg-sidebar-primary text-sidebar-primary-foreground">
      <mat-icon class="mr-3">dashboard</mat-icon>
      Dashboard
    </a>
  </li>
  <li>
    <a href="/bedarfs"
       class="flex items-center px-3 py-2 rounded-md text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground">
      <mat-icon class="mr-3">work</mat-icon>
      Bedarfs
    </a>
  </li>
  <li>
    <a href="/companies"
       class="flex items-center px-3 py-2 rounded-md text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground">
      <mat-icon class="mr-3">business</mat-icon>
      Companies
    </a>
  </li>
</ul>

```bash

  </div>
</nav>

```bash

<nav class="bg-sidebar border-r border-sidebar-border h-full">
  <div class="p-4">

```bash

<h2 class="text-sidebar-foreground text-lg font-semibold mb-4">
  Bau Platform
</h2>

```bash

```bash

<ul class="space-y-2">
  <li>
    <a href="/dashboard"
       class="flex items-center px-3 py-2 rounded-md bg-sidebar-primary text-sidebar-primary-foreground">
      <mat-icon class="mr-3">dashboard</mat-icon>
      Dashboard
    </a>
  </li>
  <li>
    <a href="/bedarfs"
       class="flex items-center px-3 py-2 rounded-md text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground">
      <mat-icon class="mr-3">work</mat-icon>
      Bedarfs
    </a>
  </li>
  <li>
    <a href="/companies"
       class="flex items-center px-3 py-2 rounded-md text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground">
      <mat-icon class="mr-3">business</mat-icon>
      Companies
    </a>
  </li>
</ul>

```bash

  </div>
</nav>

```bash

## Accessibility


### Contrast Ratios


- **Text on background**: WCAG AA compliant (4.5:1 minimum)
- **Large text**: WCAG AA compliant (3:1 minimum)
- **Interactive elements**: Clear focus states with ring colors

### Focus Management


```css

## Accessibility


### Contrast Ratios


- **Text on background**: WCAG AA compliant (4.5:1 minimum)
- **Large text**: WCAG AA compliant (3:1 minimum)
- **Interactive elements**: Clear focus states with ring colors

### Focus Management


```css
/*Focus styles using design tokens*/
.focus-visible {
  outline: 2px solid oklch(var(--ring));
  outline-offset: 2px;
}

/*Button focus*/
button:focus-visible {
  ring: 2px oklch(var(--ring));
  ring-offset: 2px;
}

```bash

/*Focus styles using design tokens*/
.focus-visible {
  outline: 2px solid oklch(var(--ring));
  outline-offset: 2px;
}

/*Button focus*/
button:focus-visible {
  ring: 2px oklch(var(--ring));
  ring-offset: 2px;
}

```bash

### Dark Mode


- Automatic detection via `prefers-color-scheme`
- Manual toggle support
- Consistent contrast ratios in both modes

## Implementation


### CSS Setup


```css

### Dark Mode


- Automatic detection via `prefers-color-scheme`
- Manual toggle support
- Consistent contrast ratios in both modes

## Implementation


### CSS Setup


```css
/*globals.css*/
@use url('./design-system.css');

- {
  box-sizing: border-box;
}

body {
  font-family: var(--font-sans);
  background-color: oklch(var(--background));
  color: oklch(var(--foreground));
  line-height: 1.5;
}

/*Smooth transitions*/
- {
  transition-property: color, background-color, border-color, text-decoration-color, fill, stroke;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

```bash

/*globals.css*/
@use url('./design-system.css');

- {
  box-sizing: border-box;
}

body {
  font-family: var(--font-sans);
  background-color: oklch(var(--background));
  color: oklch(var(--foreground));
  line-height: 1.5;
}

/*Smooth transitions*/
- {
  transition-property: color, background-color, border-color, text-decoration-color, fill, stroke;
  transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
  transition-duration: 150ms;
}

```bash

### Angular Material Integration


See [Coding Standards - Angular Material Theme Integration](./coding-standards.md#angular-material-theme-integration)

### Tailwind Configuration


See [Coding Standards - Tailwind Integration](./coding-standards.md#tailwind-integration)

## Design Tokens Export


### For Figma


```json

### Angular Material Integration


See [Coding Standards - Angular Material Theme Integration](./coding-standards.md#angular-material-theme-integration)

### Tailwind Configuration


See [Coding Standards - Tailwind Integration](./coding-standards.md#tailwind-integration)

## Design Tokens Export


### For Figma


```json
{
  "colors": {

```bash

"primary": {
  "value": "oklch(0.6324 0.1363 157.8607)",
  "type": "color"
},
"background": {
  "value": "oklch(1 0 0)",
  "type": "color"
}

```bash

  }
}

```bash

{
  "colors": {

```bash

"primary": {
  "value": "oklch(0.6324 0.1363 157.8607)",
  "type": "color"
},
"background": {
  "value": "oklch(1 0 0)",
  "type": "color"
}

```bash

  }
}

```bash

### For Developers


```typescript

### For Developers


```typescript

export const designTokens = {
  colors: {

```bash

primary: 'oklch(0.6324 0.1363 157.8607)',
background: 'oklch(1 0 0)',
// ... other tokens

```bash

  },
  spacing: {

```bash

radius: '0.625rem'

```bash

  },
  typography: {

```bash

fontSans: 'var(--font-sans)'

```bash

  }
} as const;

```bash

export const designTokens = {
  colors: {

```bash

primary: 'oklch(0.6324 0.1363 157.8607)',
background: 'oklch(1 0 0)',
// ... other tokens

```bash

  },
  spacing: {

```bash

radius: '0.625rem'

```bash

  },
  typography: {

```bash

fontSans: 'var(--font-sans)'

```bash

  }
} as const;

```bash

## Related Documentation


- [Coding Standards](./coding-standards.md)
- [Frontend Development Guide](./development.md)
- [Component Library](../05-building-blocks/frontend-architecture.md)

## Related Documentation


- [Coding Standards](./coding-standards.md)
- [Frontend Development Guide](./development.md)
- [Component Library](../05-building-blocks/frontend-architecture.md)
