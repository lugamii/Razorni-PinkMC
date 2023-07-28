package dev.razorni.hcfactions.extras.framework.commands;

import dev.razorni.hcfactions.commands.CommandManager;
import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.extras.framework.commands.extra.TabCompletion;
import dev.razorni.hcfactions.utils.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class Command extends Module<CommandManager> {
    private static ExecutorService ASYNC_EXECUTOR;

    static {
        ASYNC_EXECUTOR = Executors.newSingleThreadExecutor();
    }

    protected List<TabCompletion> completions;
    protected String permissible;
    protected boolean async;
    protected Map<String, Argument> arguments;
    protected String name;

    public Command(CommandManager manager, String name) {
        super(manager);
        this.name = name;
        this.permissible = null;
        this.async = false;
        this.arguments = new HashMap<>();
        this.completions = new ArrayList<>();
    }

    public abstract List<String> aliases();

    public Double getDouble(String number) {
        try {
            return Double.parseDouble(number);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public void sendMessage(CommandSender sender, String... args) {
        for (String s : args) {
            sender.sendMessage(CC.t(s));
        }
    }

    public abstract List<String> usage();

    private boolean hasPerm(CommandSender sender, Argument arg) {
        return (arg.getPermissible() == null && this.permissible == null) || (arg.getPermissible() != null && sender.hasPermission(arg.getPermissible())) || ((this.permissible == null || !sender.hasPermission(this.permissible) || arg.getPermissible() == null || sender.hasPermission(arg.getPermissible())) && this.permissible != null && sender.hasPermission(this.permissible));
    }

    public String getName() {
        return this.name;
    }

    public BukkitCommand asBukkitCommand() {
        BukkitCommand command = new BukkitCommand(this.name) {
            public List<String> tabComplete(CommandSender sender, String name, String[] args) throws IllegalArgumentException {
                List<String> complets = Command.this.tabComplete(sender, args);
                if (complets != null) {
                    return complets;
                }
                return (List<String>) super.tabComplete(sender, name, args);
            }

            public boolean execute(CommandSender sender, String cmd, String[] args) {
                if (Command.this.async) {
                    Command.ASYNC_EXECUTOR.execute(() -> Command.this.execute(sender, args));
                    return true;
                }
                Command.this.execute(sender, args);
                return true;
            }
        };
        if (!this.aliases().isEmpty()) {
            command.setAliases(this.aliases());
        }
        return command;
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        String other = args[args.length - 1];
        if (args.length == 1 && !this.arguments.isEmpty()) {
            List<String> list = new ArrayList<String>();
            for (Argument arg : this.arguments.values()) {
                if (this.hasPerm(sender, arg)) {
                    list.addAll(arg.getNames());
                }
            }
            return list.stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
        }
        if (!this.arguments.isEmpty()) {
            String[] array = Arrays.copyOfRange(args, 1, args.length);
            Argument arg = this.arguments.get(args[0]);
            if (arg == null) {
                return null;
            }
            List<String> lines = arg.tabComplete(sender, array);
            if (this.hasPerm(sender, arg) && lines != null && !lines.isEmpty()) {
                return lines;
            }
        }
        if (!this.completions.isEmpty()) {
            if (this.permissible != null && !sender.hasPermission(this.permissible)) {
                return null;
            }
            List<String> lines = new ArrayList<String>();
            for (TabCompletion completion : this.completions) {
                if (completion.getArg() != args.length - 1) {
                    continue;
                }
                if (completion.getPermission() != null && !sender.hasPermission(completion.getPermission())) {
                    continue;
                }
                lines.addAll(completion.getNames());
            }
            if (!lines.isEmpty()) {
                return lines.stream().filter(s -> s.regionMatches(true, 0, other, 0, other.length())).collect(Collectors.toList());
            }
        }
        return null;
    }

    public void execute(CommandSender sender, String[] args) {
        if (this.permissible != null && !sender.hasPermission(this.permissible)) {
            sender.sendMessage(Config.INSUFFICIENT_PERM);
            return;
        }
        if (args.length == 0) {
            this.sendUsage(sender);
            return;
        }
        String[] array = Arrays.copyOfRange(args, 1, args.length);
        if (!this.arguments.containsKey(args[0])) {
            this.sendUsage(sender);
            return;
        }
        Argument arg = this.arguments.get(args[0]);
        if (arg.isAsync()) {
            Command.ASYNC_EXECUTOR.execute(() -> arg.execute(sender, array));
            return;
        }
        arg.execute(sender, array);
    }

    public Integer getInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public void handleArguments(List<Argument> args) {
        for (Argument arg : args) {
            arg.getNames().forEach(argument -> this.arguments.put(argument, arg));
        }
    }

    public void sendUsage(CommandSender sender) {
        for (String s : this.usage()) {
            sender.sendMessage(CC.t(s));
        }
    }
}
