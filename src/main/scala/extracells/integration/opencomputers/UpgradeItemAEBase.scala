package extracells.integration.opencomputers

import cpw.mods.fml.common.Optional.{Interface, InterfaceList, Method}
import li.cil.oc.api.CreativeTab
import li.cil.oc.api.driver.item.{HostAware, Slot}
import li.cil.oc.api.driver.{EnvironmentAware, EnvironmentHost}
import li.cil.oc.api.internal.{Drone, Robot}
import li.cil.oc.api.network.{Environment, ManagedEnvironment}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{EnumRarity, Item, ItemStack}
import net.minecraft.nbt.NBTTagCompound

@InterfaceList(Array(
  new Interface(iface = "li.cil.oc.api.driver.item.HostAware", modid = "OpenComputers", striprefs = true),
  new Interface(iface = "li.cil.oc.api.driver.EnvironmentAware", modid = "OpenComputers", striprefs = true)
))
trait UpgradeItemAEBase extends Item with HostAware with EnvironmentAware{

  @Method(modid = "OpenComputers")
  override def setCreativeTab(creativeTabs: CreativeTabs): Item ={
    super.setCreativeTab(CreativeTab.instance)
  }

  @Method(modid = "OpenComputers")
  override def tier(stack: ItemStack): Int =
    stack.getItemDamage match {
      case 0 => 2
      case 1 => 1
      case _ => 0
  }

  @Method(modid = "OpenComputers")
  override def slot(stack: ItemStack): String = Slot.Upgrade

  @Method(modid = "OpenComputers")
  override def worksWith(stack: ItemStack): Boolean = stack != null && stack.getItem == this

  @Method(modid = "OpenComputers")
  override def createEnvironment(stack: ItemStack, host: EnvironmentHost): ManagedEnvironment = {
    if (stack != null && stack.getItem == this && worksWith(stack, host.getClass))
      new UpgradeAE(host)
    else
      null
  }

  @Method(modid = "OpenComputers")
  override def getRarity (stack: ItemStack) =
    stack.getItemDamage match {
      case 0 => EnumRarity.rare
      case 1 => EnumRarity.uncommon
      case _ => super.getRarity(stack)
  }

  @Method(modid = "OpenComputers")
  override def dataTag(stack: ItemStack) = {
    if (!stack.hasTagCompound) {
      stack.setTagCompound(new NBTTagCompound)
    }
    val nbt: NBTTagCompound = stack.getTagCompound
    if (!nbt.hasKey("oc:data")) {
      nbt.setTag("oc:data", new NBTTagCompound)
    }
    nbt.getCompoundTag("oc:data")
  }

  @Method(modid = "OpenComputers")
  override def worksWith(stack: ItemStack, host: Class[_ <: EnvironmentHost]): Boolean =
    worksWith(stack) && host != null && (classOf[Robot].isAssignableFrom(host) || classOf[Drone].isAssignableFrom(host))

  @Method(modid = "OpenComputers")
  override def providedEnvironment(stack: ItemStack): Class[_ <: Environment] = {
    if (stack != null && stack.getItem == this)
      classOf[UpgradeAE]
    else
      null
  }

}
