package one.coffee.callbacks.handlers;

import chat.tamtam.botapi.model.Message;
import one.coffee.ParentClasses.Result;
import one.coffee.callbacks.CallbackResult;
import one.coffee.callbacks.KeyboardCallbackHandler;
import one.coffee.keyboards.TestYesNoKeyBoard;
import one.coffee.keyboards.buttons.ButtonAnnotation;
import one.coffee.keyboards.buttons.NoButton;
import one.coffee.keyboards.buttons.YesButton;
import org.springframework.stereotype.Component;

@Component
public class TestYesNoKeyboardCallbackHandler extends KeyboardCallbackHandler {
    @Override
    public Class<TestYesNoKeyBoard> getKeyboardPrefix() {
        return TestYesNoKeyBoard.class;
    }

    @SuppressWarnings("unused")
    @ButtonAnnotation(YesButton.class)
    public CallbackResult YesTest(Message message) {
        System.out.println("Yes");
        return new CallbackResult(Result.ResultState.SUCCESS);
    }

    @SuppressWarnings("unused")
    @ButtonAnnotation(NoButton.class)
    public CallbackResult NoTest(Message message) {
        System.out.println("No");
        return new CallbackResult(Result.ResultState.SUCCESS);
    }
}
