# MorePowerGems

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/XqguI8fH?logo=modrinth&label=downloads&color=00AF5C)](https://modrinth.com/plugin/morepowergems)
[![Modrinth Version](https://img.shields.io/modrinth/v/XqguI8fH?logo=modrinth&label=version&color=00AF5C)](https://modrinth.com/plugin/morepowergems)
[![Modrinth Followers](https://img.shields.io/modrinth/followers/XqguI8fH?logo=modrinth&label=followers&color=00AF5C)](https://modrinth.com/plugin/morepowergems)
[![GitHub Issues](https://img.shields.io/github/issues/LeonardisG/MorePowerGems)](https://github.com/LeonardisG/MorePowerGems/issues)
[![License](https://img.shields.io/github/license/LeonardisG/MorePowerGems)](LICENSE)

An unofficial expansion pack for the [PowerGems](https://modrinth.com/plugin/powergems) plugin, which adds **10 new unique gems** with powerful abilities!

<img width="304" height="60" alt="MorePowerGems Banner" src="https://github.com/user-attachments/assets/80b6bfce-88d6-46e3-aaaa-6f758bfc6131" /> 

Original idea by [@rlxck](https://github.com/rlxck) | [Original PR](https://github.com/ISeal-plugin-developement/PowerGems/issues/61)

---

## 📋 Requirements & Compatibility

- **Required Dependencies:** 
  - [PowerGems](https://modrinth.com/plugin/powergems) (v3.6.1.1+)
  - [SealLib](https://modrinth.com/plugin/seallib) (v1.2.0.1+)
- **Server Software:** Spigot, Paper, Purpur
- **Minecraft Version:** 1.21+
- **Java Version:** 21+

---

## 📦 Installation

1. Download **SealLib**, **PowerGems**, and **MorePowerGems**
2. Place all `.jar` files in your server's `plugins/` folder
3. Restart your server
4. Install the [resource pack](https://modrinth.com/collection/Klw3bLg2) (replaces official PowerGems textures)
   - Add to `server.properties` or distribute to players

---

## Gems Overview

### **Affluence Gem** 
- **Right Click:** Haste II for 60s
- **Shift Click:** Halve all villager trade prices
- **Left Click:** Double ores and mob drops for 60s
- **Passive:** Hero of the Village

### **Wither Gem** 
- **Right Click:** Reduced damage + projectile blocking
- **Shift Click:** Explosion giving enemies Glowing + Wither
- **Left Click:** Shoot 3 Wither skulls rapidly
- **Passive:** Regeneration

### **Shulker Gem** 
- **Right Click:** Armor toughness boost for 20s
- **Shift Click:** Levitation II on nearby players (25 blocks, 10s)
- **Left Click:** Shoot shulker projectiles
- **Passive:** Resistance

### **Poison Gem** 
- **Right Click:** Shoot debuff arrow (50% Instant Damage II / 50% Poison II)
- **Shift Click:** Clear all negative effects
- **Left Click:** Self Regeneration II + target Slow Falling + Slowness
- **Passive:** Regeneration

### **Ruin Gem** 
- **Right Click:** Mossify nearby terrain (10 blocks) + gain Strength
- **Shift Click:** Spawn multiplying silverfish
- **Left Click:** Grapple to walls/ceilings for 2s
- **Passive:** Jump Boost

### **Amethyst Gem** 
- **Right Click:** Throw amethyst shard
- **Shift Click:** Illuminate nearby players with Glowing
- **Left Click:** Trap player in amethyst cage (non-lethal damage)
- **Passive:** Absorption

### **Brezze Gem** 
- **Right Click:** Dash forward
- **Shift Click:** Summon breezes
- **Left Click:** Launch wind charges
- **Passive:** No fall damage

### **Ender Gem** 
- **Right Click:** Random nearby teleport
- **Shift Click:** Summon dragon breath
- **Left Click:** Speed II + Strength II + Haste IV
- **Passive:** Night Vision

### **Magic Gem** 
- **Right Click:** Summon Evoker Fangs
- **Shift Click:** Spawn loyal Vexes
- **Left Click:** Temporary creative flight
- **Passive:** Haste

### **Mech Gem** 
- **Right Click:** Create explosion
- **Shift Click:** Protective magma box
- **Left Click:** Place temporary lava
- **Passive:** Fire Resistance

---

## 🛠️ Building from Source

```bash
git clone https://github.com/LeonardisG/MorePowerGems.git
cd MorePowerGems
mvn clean package
```

The compiled JAR will be in `target/MorePowerGems-<version>.jar`

---

## 🤝 Support & Contributing

- **Discord:** [PowerGems Discord Server](https://discord.iseal.dev/) - Ping @LeonardisG for support
- **Issues:** [GitHub Issues](https://github.com/LeonardisG/MorePowerGems/issues)
- **Contributing:** Pull requests are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 📝 License

This project is licensed under the Creative Commons Attribution Share Alike 4.0 International License - see the [LICENSE](LICENSE) file for details.

---

## ⚠️ Important Notes

- The included resource pack **replaces** the official PowerGems textures
- Don't use both resource packs simultaneously
- Make sure to update PowerGems and SealLib to the latest versions for best compatibility
