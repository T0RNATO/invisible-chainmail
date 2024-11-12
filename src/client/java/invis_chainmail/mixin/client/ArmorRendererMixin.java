package invis_chainmail.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorRendererMixin<T extends LivingEntity> {
	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"), method = "renderArmor")
	private Item init(Item item, @Cancellable CallbackInfo ci, @Local(argsOnly = true) T entity) {
		if (entity.hasStatusEffect(StatusEffects.INVISIBILITY) && (
				item.equals(Items.CHAINMAIL_BOOTS) ||
				item.equals(Items.CHAINMAIL_LEGGINGS)||
				item.equals(Items.CHAINMAIL_CHESTPLATE) ||
				item.equals(Items.CHAINMAIL_HELMET)))
		{
			ci.cancel();
		}
		return item;
	}
}