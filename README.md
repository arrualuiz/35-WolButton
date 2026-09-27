# WolButton

App Android simples, com um botão, que liga o PC pela rede usando Wake-on-LAN (envia o "pacote mágico" por UDP).

## Configuração

Na primeira vez, preencha os três campos. Eles ficam salvos no aparelho.

- **MAC da placa de rede do PC**: no Windows, rode `ipconfig /all` e copie o "Endereço Físico" da placa cabeada (ex.: `AA-BB-CC-DD-EE-FF` ou `AA:BB:CC:DD:EE:FF`).
- **Broadcast**: o endereço de broadcast da rede de casa. Na maioria dos roteadores é `192.168.0.255` ou `192.168.1.255`.
- **Porta**: `9` (padrão do Wake-on-LAN).

No PC, o Wake-on-LAN precisa estar ativo na BIOS/UEFI e nas propriedades da placa de rede ("Permitir que este dispositivo ative o computador").

## Como abrir no Android Studio

1. Abra o Android Studio.
2. Selecione **Open**.
3. Escolha a pasta `WolButton`.
4. Aguarde o Gradle Sync.
5. Conecte o Android com depuração USB ativada.
6. Clique em **Run**.

## Como testar

1. Instale o app no Android.
2. Deixe o celular no mesmo Wi-Fi do PC.
3. Desligue o PC pelo Windows.
4. Abra o app e toque em **Ligar PC**.

Se não funcionar com o PC desligado, teste primeiro com ele em suspensão.
