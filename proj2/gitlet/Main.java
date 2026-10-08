package gitlet;

import java.util.Arrays;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?

        if(args==null || args.length==0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs("init", args, 1);
                Repository.initCommand();
                break;

            case "add":
                // TODO: handle the `add [filename]` command
                /**
                if(args.length<2){
                    throw new GitletException(
                            String.format("Invalid number of arguments for: add command"));
                }
                */
                validateNumArgs("add", args, 2);
                Repository.addCommand(args[1]);
                break;

            case "commit":
                validateNumArgs("commit", args, 2);

                Repository.commit(args[1]);
                break;

            case "rm":
                validateNumArgs("rm", args, 2);
                Repository.rm(args[1]);
                break;

            case "log":
                validateNumArgs("log", args, 1);
                Repository.log();
                break;

            case "global-log":
                validateNumArgs("global-log", args, 1);
                Repository.global_log();
                break;

            case "find":
                validateNumArgs("find", args, 2);
                Repository.find(args[1]);
                break;

            case "status":
                validateNumArgs("status", args, 1);
                Repository.status();
                break;

            case "checkout":
                Repository.checkout(args);
                break;

            case "branch":
                validateNumArgs("branch", args, 2);
                Repository.branch(args[1]);
                break;
                
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                Repository.rm_branch(args[1]);
                break;

            case "reset":
                validateNumArgs("reset", args, 2);
                Repository.reset(args[1]);
                break;

            case "merge":
                validateNumArgs("merge", args, 2);
                Repository.merge(args[1]);
                break;

            default:
                System.out.println("No command with that name exists.");

        }
    }
    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new GitletException(
                    String.format("Invalid number of arguments for: %s.", cmd));
        }
    }

}
