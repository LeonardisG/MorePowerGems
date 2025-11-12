# Contributing to MorePowerGems
## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Development Setup](#development-setup)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Commit Message Guidelines](#commit-message-guidelines)

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the [issue list](https://github.com/LeonardisG/MorePowerGems/issues) as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible using my bug report template.

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please use our feature request template and provide:

- A clear and descriptive title
- A detailed description of the proposed feature
- Examples of how the feature would be used
- Why this feature would be useful

### Your First Code Contribution

Unsure where to begin? You can start by looking through `good-first-issue` and `help-wanted` issues:

- **Good first issues** - issues which should only require a few lines of code
- **Help wanted issues** - issues which are a bit more involved

## Development Setup

1. **Fork and Clone**
   ```bash
   git clone https://github.com/YOUR-USERNAME/MorePowerGems.git
   cd MorePowerGems
   ```

2. **Requirements**
   - Java 21 JDK
   - Maven 3.6+
   - Git

3. **Build the Project**
   ```bash
   mvn clean package
   ```

4. **Testing**
   - Set up a test server (Paper/Spigot 1.21+)
   - Install PowerGems and SealLib
   - Copy your built JAR to the plugins folder
   - Test your changes thoroughly

## Pull Request Process

1. **Create a Branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Your Changes**
   - Write clean, documented code
   - Follow the coding standards below
   - Test your changes thoroughly

3. **Commit Your Changes**
   ```bash
   git commit -m "feat: add new feature"
   ```
   See [Commit Message Guidelines](#commit-message-guidelines) below

4. **Push to Your Fork**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Open a Pull Request**
   - Use the pull request template
   - Link any related issues
   - Describe your changes in detail
   - Include screenshots if applicable

6. **Code Review**
   - Wait for maintainer review
   - Address any requested changes
   - Be responsive to feedback

## Coding Standards

### Java Style Guide

- **Indentation:** 4 spaces (no tabs)
- **Line Length:** Max 120 characters
- **Naming Conventions:**
  - Classes: `PascalCase`
  - Methods/Variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
  - Packages: `lowercase`

### Best Practices

- Write self-documenting code
- Add comments for complex logic
- Use meaningful variable names
- Keep methods focused and small
- Follow SOLID principles
- Handle exceptions appropriately
- Never suppress warnings without good reason

### Example

```java
public class ExampleGem extends Gem {
    
    private static final int COOLDOWN_SECONDS = 10;
    
    /**
     * Handles the right-click ability for this gem.
     * 
     * @param player The player using the gem
     */
    @Override
    public void onRightClick(Player player) {
        if (isOnCooldown(player)) {
            player.sendMessage("§cAbility is on cooldown!");
            return;
        }
        
        // Ability logic here
        applyEffect(player);
        setCooldown(player, COOLDOWN_SECONDS);
    }
}
```

## Commit Message Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/) specification:

### Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- **feat:** A new feature
- **fix:** A bug fix
- **docs:** Documentation changes
- **style:** Code style changes (formatting, no code change)
- **refactor:** Code refactoring
- **perf:** Performance improvements
- **test:** Adding or updating tests
- **chore:** Maintenance tasks

### Examples

```
feat(magic-gem): add creative flight ability

Add a left-click ability that grants temporary creative flight for 10 seconds.
Includes cooldown and particle effects.

Closes #42
```

```
fix(ruin-gem): resolve grappling hook not releasing

The grappling hook was not properly releasing players after 2 seconds.
Added proper task cancellation and state cleanup.

Fixes #38
```

## Questions?

Feel free to ask questions in:
- [GitHub Discussions](https://github.com/LeonardisG/MorePowerGems/discussions)
- [PowerGems Discord](https://discord.iseal.dev/) - Ping @LeonardisG

---

Thank you for contributing to MorePowerGems! 💎

