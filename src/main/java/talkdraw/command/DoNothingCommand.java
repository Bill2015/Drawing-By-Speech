package talkdraw.command;

import talkdraw.App;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;

public class DoNothingCommand extends Command{
    public DoNothingCommand(App APP, CommandAttribute yamlLoader) {
        super(APP, yamlLoader);
    }
    @Override public NextCommand execute( String ... args ) {  return new NextCommand( getClass(), Phase.None ); }
    @Override protected NextCommand success( String ... args ) { return null; }
    @Override protected NextCommand failure( String ... args ) { return null; }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

}