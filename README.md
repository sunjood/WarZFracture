# WarZFracture - 真实骨折系统插件

## 插件概述

WarZFracture是一款为Minecraft服务器打造的高度真实的骨折系统插件，灵感来源于《三角洲行动》等FPS游戏。该插件为您的服务器带来更加真实的受伤机制，玩家在受到伤害时可能会导致不同部位的骨折，从而影响游戏体验。

## 主要特性

### 多部位骨折系统
- **头部骨折**：导致视觉模糊（混乱效果）、最大生命值降低和虚弱效果
- **手臂骨折**：降低攻击伤害和挖掘速度（挖掘疲劳效果）
- **腿部骨折**：显著降低移动速度和跳跃能力

### 真实的治疗系统
- **手术包**：用于彻底治愈骨折，使用需要一定时间，可配置多种类型
- **止痛药**：暂时缓解骨折带来的负面效果，但不会治愈骨折

### 高度可配置
- 每种骨折的概率、效果和持续时间均可在配置文件中自定义
- 可配置多种手术包和止痛药，包括使用时间、效果持续时间和音效
- 支持多语言系统（目前支持中文和英文）

### 其他功能
- 摔落伤害系统：从高处摔落可能导致骨折，高度和伤害倍数可配置
- 骨折状态持久化：玩家的骨折状态会被保存，重新登录后依然存在
- 命令系统：管理员可以通过命令管理玩家的骨折状态和获取治疗物品

## 配置示例

```yaml
# 骨折系统配置
fracture:
  # 头部骨折
  headFracture:
    probability: 0.05  # 受伤时头部骨折概率 (5%)
    maxHealthPenalty: 2.0  # 最大生命值减少量
    maxFractures: 1  # 头部骨折最大数量

  # 手臂骨折
  armFracture:
    probability: 0.15  # 受伤时手臂骨折概率 (15%)
    miningSpeedMultiplier: 0.5  # 挖掘速度减慢倍数
    maxFractures: 2  # 手臂骨折最大数量

  # 腿部骨折
  legFracture:
    probability: 0.20  # 受伤时腿部骨折概率 (20%)
    walkSpeedMultiplier: 0.5  # 移动速度减慢至正常速度的比例
    maxFractures: 2  # 腿部骨折最大数量
```

## 治疗物品

### 手术包
手术包用于彻底治愈骨折，使用时需要一定时间，期间如果切换物品或死亡将取消使用。手术包有使用次数限制，用完后将消失。

### 止痛药
止痛药可以暂时缓解骨折带来的负面效果，但不会治愈骨折。效果持续时间结束后，骨折的负面效果将重新生效。

## 命令

- `/fracture` 或 `/fz` - 查看骨折状态
- `/fracture heal` - 治愈所有骨折（需要权限）
- `/fracture reload` - 重新加载配置（需要权限）
- `/fracture give <类型>` - 获取治疗物品（需要权限）

## 权限

- `warzfracture.reload` - 允许重新加载插件配置
- `warzfracture.give` - 允许获取治疗物品

## 作者信息

- 作者: Crazy_Jky
- QQ: 1285988665
## papi变量
- %warzfracture_has_fracture% - 返回玩家是否有任何骨折（true/false）
- %warzfracture_head_fractures% - 返回玩家头部骨折数量
- %warzfracture_arm_fractures% - 返回玩家手臂骨折数量
- %warzfracture_leg_fractures% - 返回玩家腿部骨折数量
- %warzfracture_total_fractures% - 返回玩家总骨折数量
- %warzfracture_has_painkiller_effect% - 返回玩家是否处于止痛药效果中（true/false）

为您的服务器带来更加真实的生存体验，让玩家在战斗和冒险中更加谨慎！