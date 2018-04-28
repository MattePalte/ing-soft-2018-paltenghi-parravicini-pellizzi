package project.ing.soft.testsocket.request;

public class EndTurnRequest implements IRequest {
    private String nickname;

    public EndTurnRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public void accept(IRequestHandler handler) throws Exception {
        handler.handle(this);
    }
}
