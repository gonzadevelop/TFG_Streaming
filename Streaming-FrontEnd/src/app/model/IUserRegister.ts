export interface IUserRegister {
  nombre: string;
  apellidos: string;
  username: string;
  email: string;
  password?: string;
  confirmPassword: string;
  genero: 'Masculino' | 'Femenino' | 'Otro' | 'Prefiero no decirlo';
  dni: string;
  telefono: number;
}
