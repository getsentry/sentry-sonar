<p align="center">
  <a href="https://sentry.io/?utm_source=github&utm_medium=logo" target="_blank">
    <picture>
      <source srcset="https://sentry-brand.storage.googleapis.com/sentry-logo-white.png" media="(prefers-color-scheme: dark)" />
      <source srcset="https://sentry-brand.storage.googleapis.com/sentry-logo-black.png" media="(prefers-color-scheme: light), (prefers-color-scheme: no-preference)" />
      <img src="https://sentry-brand.storage.googleapis.com/sentry-logo-black.png" alt="Sentry" width="280">
    </picture>
  </a>
</p>

# Sentry Plugin for Sonar

An experimental SonarQube plugin compatible with SonarQube 9.x that reports errors from Sentry as external issues for
code analysis.

## Configuration

Per Sonar project, this plugin requires configuration to retrieve issues from Sentry:
- An integration API key. See [Sentry docs](https://docs.sentry.io/product/integrations/integration-platform/) for more information on generating an API key.
- The organization and project to fetch errors from.
- A path mapping for absolute paths reported in Sentry to relative source paths.

## Resources

- [Sentry](https://sentry.io/welcome/)
- [Documentation](https://docs.sentry.io/)
- [Sentry Source](https://github.com/getsentry/sentry/)
