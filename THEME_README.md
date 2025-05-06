# Theme Redesign Documentation

## Theme Options

The application now supports two theme options:

1. **Light Theme**
2. **Dark Theme**

## Color Palette

### Light Theme

- Primary Dark: `#213448` (Dark blue-gray)
- Primary: `#547792` (Medium blue-gray)
- Primary Light: `#94B4C1` (Light blue-gray)
- Background: `#ECEFCA` (Light beige/cream)

### Dark Theme

The dark theme uses an inverted version of the light theme palette:

- Primary Dark: `#ECEFCA` (Light beige/cream)
- Primary: `#94B4C1` (Light blue-gray)
- Primary Light: `#547792` (Medium blue-gray)
- Background: `#213448` (Dark blue-gray)

## Theme Structure

The theme system is organized as follows:

- **Light Theme (Default)**: Defined in `colors_light.xml` and referenced in main `colors.xml`
- **Dark Theme**: Defined in `colors_dark.xml` and used in the night version through `values-night/colors.xml`

The application automatically switches between light and dark themes based on the system settings.

## Implementation Details

1. **Color References**: All color references in the app use the abstracted color names (e.g., `@color/primary`, `@color/background`) rather than direct color values.
2. **Night Mode Support**: Night resources are stored in the `values-night` directory.
3. **Material Components**: The app uses Material Components with the M3 theme as base.

## Usage Guidelines

When developing new UI components, always use the themed color resources:

- `@color/primary` for primary brand elements
- `@color/primary_dark` for darker variants and borders
- `@color/primary_light` for lighter accents
- `@color/background` for screen backgrounds
- `@color/text_primary` for main text
- `@color/text_secondary` for secondary text
