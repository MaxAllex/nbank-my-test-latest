package api.http.facade;

public interface HTTPRequest<Rec, Res> {
  Res post(Rec model);

  Res get();

  Res put(Rec model);

  Res patch(long id, Rec model);

  Res delete(long id);
}
