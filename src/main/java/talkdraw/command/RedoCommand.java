package talkdraw.command;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;

/**【指令】 《重作相關指令》
    * <p>復原下一步的動作</p>*/
public class RedoCommand extends Command{

    /** 建構子 */
    public RedoCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                return (APP.redo() ? success() : failure());
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( "成功重作", App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( "沒有可重作的動作" , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

}