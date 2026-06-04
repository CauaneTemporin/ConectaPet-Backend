-- ============================================================
-- SEED INICIAL — ConectaPet
-- Senha de todos os usuários: 123456
-- ============================================================

-- ── USUÁRIOS ─────────────────────────────────────────────────
INSERT OR IGNORE INTO users (name, email, password, city, role, phone, avatar_url, bio, token_version, created_at)
VALUES
    (
        'Fernanda Isabelle Barros',
        'fernanda_isabelle_barros@easytechconsultoria.com.br',
        '$2b$10$JNxVs6YiJ3.9MWQFONwfjepMexs40sysN1n/CDzFsVQm4RG3CaVLe',
        'São Paulo',
        'ADMIN',
        '(11) 91111-1111',
        NULL,
        'Administrador global da plataforma.',
        1,
        CURRENT_TIMESTAMP
    ),
    (
        'Filipe Nicolas Leonardo Bernardes',
        'filipe_nicolas_bernardes@leandroreis.com',
        '$2b$10$JNxVs6YiJ3.9MWQFONwfjepMexs40sysN1n/CDzFsVQm4RG3CaVLe',
        'São Paulo',
        'USER',
        '(11) 92222-2222',
        'https://exemplo.com/avatar1.jpg',
        'Responsável pela ONG Amigo Animal.',
        1,
        CURRENT_TIMESTAMP
    ),
    (
        'Bruno Gael Brito',
        'bruno_gael_brito@bfgadvogados.com',
        '$2b$10$JNxVs6YiJ3.9MWQFONwfjepMexs40sysN1n/CDzFsVQm4RG3CaVLe',
        'Rio de Janeiro',
        'USER',
        '(21) 93333-3333',
        NULL,
        NULL,
        1,
        CURRENT_TIMESTAMP
    ),
    (
        'Gael Lorenzo da Conceição',
        'gael-daconceicao88@mpv.org.br',
        '$2b$10$JNxVs6YiJ3.9MWQFONwfjepMexs40sysN1n/CDzFsVQm4RG3CaVLe',
        'Belo Horizonte',
        'USER',
        '(31) 94444-4444',
        NULL,
        NULL,
        1,
        CURRENT_TIMESTAMP
    );

    (
        'Amigo Animal',
        'contato@amigoanimal.org',
        '$2b$10$JNxVs6YiJ3.9MWQFONwfjepMexs40sysN1n/CDzFsVQm4RG3CaVLe',
        'São Paulo',
        'USER',
        '(11) 3333-4444',
        NULL,
        NULL,
        1,
        CURRENT_TIMESTAMP
    ),
    (
        'Patinhas',
        'contato@patinhas.org',
        '$2b$10$JNxVs6YiJ3.9MWQFONwfjepMexs40sysN1n/CDzFsVQm4RG3CaVLe',
        'Rio de Janeiro',
        'USER',
        '(21) 3333-5555',
        NULL,
        NULL,
        1,
        CURRENT_TIMESTAMP
    ),
    (
        'Resgata Pet',
        'contato@resgatepet.org',
        '$2b$10$JNxVs6YiJ3.9MWQFONwfjepMexs40sysN1n/CDzFsVQm4RG3CaVLe',
        'Belo Horizonte',
        'USER',
        '(31) 3333-6666',
        NULL,
        NULL,
        1,
        CURRENT_TIMESTAMP
    );

-- ── ONGs ─────────────────────────────────────────────────────
INSERT OR IGNORE INTO ongs (
    cnpj, razao_social, nome_fantasia, email, telefone,
    cep, endereco, cidade, estado,
    descricao, historia, missao,
    logo_url, pix_qr_code_url,
    facebook, whatsapp, instagram, youtube, tiktok, telegram,
    status, solicitado_por, created_at, updated_at
)
VALUES
    (
        '12.345.678/0001-99',
        'Instituto Amigo dos Animais',
        'Amigo Animal',
        'contato@amigoanimal.org',
        '(11) 3333-4444',
        '01310-100',
        'Av. Paulista, 1000 - Bela Vista',
        'São Paulo',
        'SP',
        'ONG dedicada ao resgate e adoção de animais abandonados na Grande SP.',
        'Fundada em 2010 por um grupo de voluntários apaixonados por animais, a Amigo Animal nasceu da necessidade de oferecer um lar temporário seguro para pets resgatados das ruas.',
        'Promover o bem-estar animal por meio de resgate, cuidado e adoção responsável.',
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'ATIVA', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ),
    (
        '98.765.432/0001-11',
        'Patinhas Felizes LTDA',
        'Patinhas',
        'contato@patinhas.org',
        '(21) 3333-5555',
        '20040-020',
        'Rua da Assembleia, 200 - Centro',
        'Rio de Janeiro',
        'RJ',
        'ONG voltada à proteção de gatos e cães em situação de rua no RJ.',
        'Desde 2015 atuamos nos bairros da Zona Sul cuidando de animais em situação de vulnerabilidade.',
        'Garantir vida digna a animais de rua por meio de castração, vacinação e adoção.',
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'ATIVA', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    ),
    (
        '11.222.333/0001-44',
        'Resgata Pet Instituto',
        'Resgata Pet',
        'contato@resgatepet.org',
        '(31) 3333-6666',
        '30112-010',
        'Av. Afonso Pena, 500 - Centro',
        'Belo Horizonte',
        'MG',
        'Atuamos no resgate emergencial de animais em situação de maus-tratos em MG.',
        NULL,
        'Resgatar e reabilitar animais vítimas de maus-tratos, promovendo adoção consciente.',
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL,
        'ATIVA', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    );
