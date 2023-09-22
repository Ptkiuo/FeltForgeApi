package net.feltmc.neoforge.patches.mixin;

import net.feltmc.neoforge.patches.interfaces.NbtAccounterInterface;
import net.minecraft.nbt.NbtAccounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NbtAccounter.class)
public abstract class NbtAccounterMixin implements NbtAccounterInterface {
    @Shadow public abstract void accountBytes(long l);

    @Override
    public String readUTF(String data) {
        accountBytes(2); //Header length
        if (data == null) return data;
        int len = data.length();
        int utflen = 0;
        for (int i = 0; i < len; i++) {
            int c = data.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) utflen += 1;
            else if (c > 0x07FF)                utflen += 3;
            else                                utflen += 2;
        }
        accountBytes(utflen);
        return data;
    }
}
