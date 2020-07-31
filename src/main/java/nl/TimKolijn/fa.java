package nl.TimKolijn;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public final class fa extends JavaPlugin implements Listener, Cancellable, CommandExecutor {
    private ArrayList<UUID> authlocked;

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this,this);
        authlocked = new ArrayList<UUID>();

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        if(player.hasPermission("2fa.staff")){
            if(!this.getConfig().contains("authcodes." + player.getUniqueId().toString())){
                GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
                GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();

                player.sendMessage("§l§6[§b§l2§eFA§6]§f For your §9G§co§eo§9g§2l§ce§f auth code click this link §e§l" + "https://api.qrserver.com/v1/create-qr-code/?data=otpauth://totp/AmethystMC:"+player.getName() +"?secret="+key.getKey() +"&issuer=AmethystMC" + " §7.");
                player.sendMessage("§l§6[§b§l2§eFA§6]§f Put this code in the google auth app before leaving.");

                this.getConfig().set("authcodes." + player.getUniqueId().toString(), key.getKey());
                this.saveConfig();
            }else{
                authlocked.add(player.getUniqueId());
                player.sendMessage("§7§l§6[§b§l2§eFA§6]§f Your account is locked for safety measures please enter your §9G§co§eo§9g§2l§ce§f auth code.(/2fa <code>)");
            }
        }
    }

    private boolean playerInputCode(Player player, int code){
        String secretKey = this.getConfig().getString("authcodes." + player.getUniqueId().toString());
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        boolean codeIsValid = googleAuthenticator.authorize(secretKey, code);
        if(codeIsValid){
            authlocked.remove(player.getUniqueId());
            return codeIsValid;
        }
    return codeIsValid;
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if(authlocked.contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
                    if(args.length == 0){
                        if(authlocked.contains(player.getUniqueId())){
                            try{
                                Integer code = Integer.parseInt(args[0]);
                                if(playerInputCode(player, code)){
                                    authlocked.remove(player.getUniqueId());
                                    player.sendMessage("§7§l§6[§b§l2§eFA§6]§f You have been §a§lUNLOCKED§f.");
                                }else {
                                    player.sendMessage("§7§l§6[§b§l2§eFA§6]§f Wrong code, your account will stay §c§lLOCKED§r§f.");
                                }
                            }catch (NumberFormatException e){
                                player.sendMessage("§l§6[§b§l2§eFA§6]§f Please write your code correct, it should contain any letters.");
                            }
                        }
                    }else{
                        player.sendMessage("§l§6[§b§l2§eFA§6]§f Please use /2fa <code>");
                    }
        }
        return true;
    }
    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }

    @EventHandler
    public void move(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(authlocked.contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void commands(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        if(authlocked.contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(authlocked.contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void blockplace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(authlocked.contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void BlockBrake(PlayerInteractEvent event){
            Player player = event.getPlayer();
        if(authlocked.contains(player.getUniqueId())){
            event.setCancelled(true);
        }
    }

}
